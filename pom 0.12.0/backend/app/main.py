from __future__ import annotations

import os, secrets, uuid
from datetime import datetime, timedelta, timezone
from typing import Optional

import jwt
import pyotp
from argon2 import PasswordHasher
from argon2.exceptions import VerifyMismatchError
from fastapi import FastAPI, Depends, HTTPException, Header
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from sqlalchemy import create_engine, String, Boolean, DateTime, Text, ForeignKey, select
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, sessionmaker, Session

DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///./prometeo.db")
JWT_SECRET = os.getenv("JWT_SECRET", "")
BOOTSTRAP_TOKEN = os.getenv("ZEUS_BOOTSTRAP_TOKEN", "")
if len(JWT_SECRET) < 32:
    raise RuntimeError("JWT_SECRET must contain at least 32 characters")
if not BOOTSTRAP_TOKEN:
    raise RuntimeError("ZEUS_BOOTSTRAP_TOKEN is required")
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False} if DATABASE_URL.startswith("sqlite") else {})
SessionLocal = sessionmaker(bind=engine, expire_on_commit=False)
hasher = PasswordHasher()
app = FastAPI(title="Prometeo COSMO API", version="1.0.0")
app.add_middleware(CORSMiddleware, allow_origins=[], allow_methods=["GET","POST","PUT","DELETE"], allow_headers=["Authorization","Content-Type"])

class Base(DeclarativeBase): pass
class User(Base):
    __tablename__ = "users"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    username: Mapped[str] = mapped_column(String(80), unique=True, index=True)
    password_hash: Mapped[str] = mapped_column(String(300))
    role: Mapped[str] = mapped_column(String(20), default="USER")
    approved: Mapped[bool] = mapped_column(Boolean, default=False)
    totp_secret: Mapped[Optional[str]] = mapped_column(String(64), nullable=True)
    active: Mapped[bool] = mapped_column(Boolean, default=True)
class SessionToken(Base):
    __tablename__ = "sessions"
    id: Mapped[str] = mapped_column(String, primary_key=True)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id"))
    expires_at: Mapped[datetime] = mapped_column(DateTime(timezone=True))
    revoked: Mapped[bool] = mapped_column(Boolean, default=False)
    mfa_pending: Mapped[bool] = mapped_column(Boolean, default=False)
class Audit(Base):
    __tablename__ = "audit"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    actor: Mapped[str] = mapped_column(String)
    action: Mapped[str] = mapped_column(String)
    target: Mapped[str] = mapped_column(String, default="")
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
class Config(Base):
    __tablename__ = "configs"
    key: Mapped[str] = mapped_column(String, primary_key=True)
    value: Mapped[str] = mapped_column(Text)
    updated_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
class Memory(Base):
    __tablename__ = "oracle_memory"
    id: Mapped[str] = mapped_column(String, primary_key=True)
    owner_id: Mapped[str] = mapped_column(ForeignKey("users.id"), index=True)
    body: Mapped[str] = mapped_column(Text)
    updated_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
Base.metadata.create_all(engine)

def db():
    s=SessionLocal()
    try: yield s
    finally: s.close()

class BootstrapIn(BaseModel):
    token: str
    username: str = Field(min_length=3, max_length=80)
    password: str = Field(min_length=14, max_length=256)
class LoginIn(BaseModel):
    username: str
    password: str
    totp: Optional[str] = None
class EnrollIn(BaseModel):
    username: str = Field(min_length=3, max_length=80)
    password: str = Field(min_length=14, max_length=256)
class TotpIn(BaseModel):
    code: str
class AdminAction(BaseModel):
    user_id: str
    role: str = "ADMIN"
    approved: bool = True
class ConfigIn(BaseModel):
    values: dict[str, str]
class MemoryIn(BaseModel):
    id: str
    body: str = Field(min_length=1, max_length=20000)

def audit(s: Session, actor: str, action: str, target: str=""):
    s.add(Audit(actor=actor, action=action, target=target)); s.commit()

def token_for(s: Session, u: User, mfa_pending: bool=False):
    sid=str(uuid.uuid4()); expires=datetime.now(timezone.utc)+timedelta(minutes=int(os.getenv("ACCESS_TOKEN_MINUTES","10")))
    s.add(SessionToken(id=sid,user_id=u.id,expires_at=expires,revoked=False,mfa_pending=mfa_pending)); s.commit()
    return jwt.encode({"sub":u.id,"sid":sid,"exp":expires},JWT_SECRET,algorithm="HS256")

def current_user(authorization: str=Header(default=""), s: Session=Depends(db), allow_mfa_pending: bool=False):
    if not authorization.startswith("Bearer "): raise HTTPException(401,"Bearer token required")
    try: data=jwt.decode(authorization[7:],JWT_SECRET,algorithms=["HS256"])
    except jwt.PyJWTError: raise HTTPException(401,"Invalid or expired session")
    sess=s.get(SessionToken,data.get("sid")); u=s.get(User,data.get("sub"))
    if not sess or sess.revoked or sess.expires_at.replace(tzinfo=timezone.utc)<datetime.now(timezone.utc) or not u or not u.active:
        raise HTTPException(401,"Session revoked or user inactive")
    if sess.mfa_pending and not allow_mfa_pending: raise HTTPException(403,"MFA setup must be completed first")
    return u

def mfa_enrollment_user(authorization: str=Header(default=""), s: Session=Depends(db)):
    if not authorization.startswith("Bearer "): raise HTTPException(401,"Bearer token required")
    try: data=jwt.decode(authorization[7:],JWT_SECRET,algorithms=["HS256"])
    except jwt.PyJWTError: raise HTTPException(401,"Invalid or expired session")
    sess=s.get(SessionToken,data.get("sid")); u=s.get(User,data.get("sub"))
    if not sess or sess.revoked or not sess.mfa_pending or not u or not u.active: raise HTTPException(403,"MFA enrollment session required")
    return u

def role_required(*roles):
    def check(u: User=Depends(current_user)):
        if u.role not in roles: raise HTTPException(403,"Insufficient role")
        return u
    return check

@app.get("/health")
def health(): return {"status":"ok","service":"prometeo-cosmo"}

@app.post("/v1/bootstrap")
def bootstrap(x: BootstrapIn, s: Session=Depends(db)):
    if not secrets.compare_digest(x.token,BOOTSTRAP_TOKEN): raise HTTPException(403,"Invalid bootstrap token")
    if s.scalar(select(User.id).limit(1)): raise HTTPException(409,"Bootstrap already completed")
    u=User(username=x.username,password_hash=hasher.hash(x.password),role="ZEUS",approved=True)
    s.add(u); s.commit(); audit(s,u.id,"BOOTSTRAP_ZEUS")
    return {"user_id":u.id,"role":u.role,"message":"ZEUS created. Configure MFA before privileged operations."}

@app.post("/v1/enroll")
def enroll(x: EnrollIn, s: Session=Depends(db)):
    if s.scalar(select(User).where(User.username==x.username)): raise HTTPException(409,"Username unavailable")
    u=User(username=x.username,password_hash=hasher.hash(x.password),role="USER",approved=False)
    s.add(u); s.commit(); audit(s,u.id,"ENROLLMENT_REQUEST")
    return {"status":"pending_approval"}

@app.post("/v1/login")
def login(x: LoginIn, s: Session=Depends(db)):
    u=s.scalar(select(User).where(User.username==x.username))
    if not u or not u.active:
        raise HTTPException(401,"Invalid credentials")
    try: hasher.verify(u.password_hash,x.password)
    except VerifyMismatchError: raise HTTPException(401,"Invalid credentials")
    if not u.approved: raise HTTPException(403,"Awaiting ZEUS approval")
    if not u.totp_secret:
        return {"mfa_setup_required":True,"enrollment_token":token_for(s,u,mfa_pending=True),"expires_in":int(os.getenv("ACCESS_TOKEN_MINUTES","10"))*60}
    if not x.totp or not pyotp.TOTP(u.totp_secret).verify(x.totp,valid_window=1): raise HTTPException(401,"Invalid MFA code")
    audit(s,u.id,"LOGIN")
    return {"access_token":token_for(s,u),"token_type":"bearer","expires_in":int(os.getenv("ACCESS_TOKEN_MINUTES","10"))*60,"role":u.role}

@app.post("/v1/mfa/setup")
def mfa_setup(u: User=Depends(mfa_enrollment_user),s: Session=Depends(db)):
    if u.totp_secret: raise HTTPException(409,"MFA already configured")
    secret=pyotp.random_base32(); u.totp_secret=secret; s.commit()
    return {"secret":secret,"otpauth_uri":pyotp.TOTP(secret).provisioning_uri(name=u.username,issuer_name=os.getenv("TOTP_ISSUER","Prometeo COSMO")),"warning":"Store this secret securely and confirm with /v1/mfa/confirm."}

@app.post("/v1/mfa/confirm")
def mfa_confirm(x: TotpIn,u: User=Depends(mfa_enrollment_user),authorization: str=Header(default=""),s: Session=Depends(db)):
    if not u.totp_secret or not pyotp.TOTP(u.totp_secret).verify(x.code,valid_window=1): raise HTTPException(400,"Invalid MFA code")
    data=jwt.decode(authorization[7:],JWT_SECRET,algorithms=["HS256"]); sess=s.get(SessionToken,data["sid"]); sess.mfa_pending=False; s.commit()
    audit(s,u.id,"MFA_ENABLED"); return {"mfa_enabled":True,"message":"MFA enabled. Log in again with password and TOTP."}

@app.post("/v1/logout")
def logout(u: User=Depends(current_user),authorization: str=Header(default=""),s: Session=Depends(db)):
    data=jwt.decode(authorization[7:],JWT_SECRET,algorithms=["HS256"]); sess=s.get(SessionToken,data["sid"]); sess.revoked=True; s.commit(); audit(s,u.id,"LOGOUT")
    return {"revoked":True}

@app.get("/v1/admin/users")
def users(u: User=Depends(role_required("ZEUS","ADE","POSEIDONE")),s: Session=Depends(db)):
    return [{"id":x.id,"username":x.username,"role":x.role,"approved":x.approved,"active":x.active} for x in s.scalars(select(User)).all()]

@app.post("/v1/admin/assign")
def assign(x: AdminAction,u: User=Depends(role_required("ZEUS","ADE","POSEIDONE")),s: Session=Depends(db)):
    target=s.get(User,x.user_id)
    if not target: raise HTTPException(404,"User not found")
    if target.role=="ZEUS": raise HTTPException(403,"ZEUS cannot be modified")
    if x.role in ("ZEUS","ADE","POSEIDONE") and u.role!="ZEUS": raise HTTPException(403,"Only ZEUS can appoint or revoke masters")
    if x.role not in ("USER","ADMIN","ADE","POSEIDONE"): raise HTTPException(400,"Invalid role")
    target.role=x.role; target.approved=x.approved; s.commit(); audit(s,u.id,"ROLE_OR_APPROVAL_CHANGE",target.id)
    return {"id":target.id,"role":target.role,"approved":target.approved}

@app.post("/v1/admin/revoke/{user_id}")
def revoke(user_id: str,u: User=Depends(role_required("ZEUS","ADE","POSEIDONE")),s: Session=Depends(db)):
    target=s.get(User,user_id)
    if not target or target.role=="ZEUS": raise HTTPException(404,"Target not found or protected")
    if u.role!="ZEUS" and target.role in ("ADE","POSEIDONE"): raise HTTPException(403,"Only ZEUS can revoke delegates")
    target.active=False
    for sess in s.scalars(select(SessionToken).where(SessionToken.user_id==user_id)).all(): sess.revoked=True
    s.commit(); audit(s,u.id,"USER_REVOKED",user_id); return {"revoked":True}

@app.get("/v1/cosmo")
def cosmo(u: User=Depends(current_user)):
    if not (u.role=="ZEUS" or (u.role=="ADMIN" and u.approved)): raise HTTPException(403,"COSMO restricted")
    return {"access":"granted","role":u.role}

@app.get("/v1/config")
def get_config(u: User=Depends(current_user),s: Session=Depends(db)):
    if u.role not in ("ZEUS","ADE","POSEIDONE"): raise HTTPException(403,"Master role required")
    return {x.key:x.value for x in s.scalars(select(Config)).all()}

@app.put("/v1/config")
def put_config(x: ConfigIn,u: User=Depends(role_required("ZEUS")),s: Session=Depends(db)):
    for k,v in x.values.items():
        if len(k)>100 or len(v)>50000: raise HTTPException(400,"Config value too large")
        row=s.get(Config,k)
        if row: row.value=v; row.updated_at=datetime.now(timezone.utc)
        else: s.add(Config(key=k,value=v))
    s.commit(); audit(s,u.id,"CONFIG_UPDATE"); return {"saved":len(x.values)}

@app.get("/v1/oracle")
def oracle(u: User=Depends(current_user),s: Session=Depends(db)):
    return [{"id":m.id,"body":m.body,"updated_at":m.updated_at} for m in s.scalars(select(Memory).where(Memory.owner_id==u.id)).all()]

@app.put("/v1/oracle")
def save_memory(x: MemoryIn,u: User=Depends(current_user),s: Session=Depends(db)):
    m=s.get(Memory,x.id)
    if m and m.owner_id!=u.id: raise HTTPException(403,"Memory belongs to another user")
    if m: m.body=x.body; m.updated_at=datetime.now(timezone.utc)
    else: s.add(Memory(id=x.id,owner_id=u.id,body=x.body))
    s.commit(); return {"saved":True,"id":x.id}

@app.get("/v1/audit")
def audit_log(u: User=Depends(role_required("ZEUS")),s: Session=Depends(db)):
    return [{"actor":a.actor,"action":a.action,"target":a.target,"created_at":a.created_at} for a in s.scalars(select(Audit).order_by(Audit.created_at.desc()).limit(500)).all()]
