from app.main import app, Base, engine
from fastapi.testclient import TestClient
import os

def test_health():
    assert TestClient(app).get("/health").status_code == 200
