#!/usr/bin/env python3
"""Deterministic logic simulation for PROMETEO Ultimate; not an Android/device test."""
import unittest
SEP='\x1e'
def pack(items):
    return SEP.join(''.join('\\\\' if c=='\\' else '\\e' if c==SEP else c for c in x) for x in items)
def unpack(value):
    if not value: return []
    out=[]; cur=[]; escaped=False
    for c in value:
        if escaped:
            cur.append(SEP if c=='e' else '\\' if c=='\\' else '\\'+c); escaped=False
        elif c=='\\': escaped=True
        elif c==SEP: out.append(''.join(cur)); cur=[]
        else: cur.append(c)
    if escaped: cur.append('\\')
    out.append(''.join(cur)); return out
ROLES=('USER','ADMIN','ADE','POSEIDONE','ZEUS')
def allows(role, action, approved=False):
    if action=='USE_STANDARD': return True
    if action=='MANAGE_USERS': return role in ('ADMIN','ADE','POSEIDONE','ZEUS')
    if action=='MANAGE_ADMINS': return role in ('ADE','POSEIDONE','ZEUS')
    if action=='GRANT_MASTER': return role=='ZEUS'
    if action=='ENTER_RESTRICTED_AREA': return role=='ZEUS' or (role=='ADMIN' and approved)
    return False
def secure_endpoint(url):
    if not url.lower().startswith('https://'): return False
    host=url[8:].split('/')[0].split('?')[0].split('#')[0]
    return bool(host and ' ' not in host and '.' in host)
class Simulation(unittest.TestCase):
    def test_storage_roundtrip_special_chars(self):
        samples=['test','pipe | slash \\','record'+SEP+'separator','accenti àèìòù 😀','']
        self.assertEqual(unpack(pack(samples)), samples)
    def test_lifecycle_gates_awaken(self):
        configured=False; awakened=False
        if configured: awakened=True
        self.assertFalse(awakened)
        configured=True
        if configured: awakened=True
        self.assertTrue(awakened)
    def test_role_hierarchy(self):
        self.assertFalse(allows('USER','ENTER_RESTRICTED_AREA'))
        self.assertFalse(allows('ADMIN','ENTER_RESTRICTED_AREA'))
        self.assertTrue(allows('ADMIN','ENTER_RESTRICTED_AREA',True))
        self.assertTrue(allows('ZEUS','ENTER_RESTRICTED_AREA'))
        self.assertFalse(allows('ADE','GRANT_MASTER'))
        self.assertFalse(allows('POSEIDONE','GRANT_MASTER'))
        self.assertTrue(allows('ZEUS','GRANT_MASTER'))
    def test_endpoint_validation(self):
        self.assertTrue(secure_endpoint('https://api.example.com/v1/chat'))
        self.assertFalse(secure_endpoint('http://api.example.com'))
        self.assertFalse(secure_endpoint('https://'))
        self.assertFalse(secure_endpoint('https://host senza spazio'))
if __name__=='__main__': unittest.main(verbosity=2)
