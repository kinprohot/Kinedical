import json
import urllib.request
import urllib.error

base = "http://localhost:8080"
creds = {
    "email": "testuser2@example.com",
    "password": "TestPass123!"
}
req = urllib.request.Request(
    base + "/api/auth/login",
    data=json.dumps(creds).encode("utf-8"),
    headers={"Content-Type": "application/json"},
    method="POST"
)

try:
    with urllib.request.urlopen(req, timeout=20) as resp:
        auth = json.loads(resp.read().decode("utf-8"))
        print("login", resp.status, auth)
except urllib.error.HTTPError as e:
    print("login fail", e.code, e.read().decode("utf-8"))
    raise SystemExit(1)

record = {
    "patientId": "6a0d2773f42e1340bfc80bf4",
    "doctorId": auth["username"],
    "visitDate": "2026-05-20T00:00:00Z",
    "recordType": "CONSULTATION",
    "status": "FINAL",
    "summary": "Test consult",
    "symptoms": ["cough", "fever"],
    "vitals": {"bloodPressure": "120/80", "heartRate": 72, "temperature": 37.0},
    "notes": "Created by doctor testuser2"
}
req2 = urllib.request.Request(
    base + "/api/medical-records",
    data=json.dumps(record).encode("utf-8"),
    headers={
        "Content-Type": "application/json",
        "Authorization": f"Bearer {auth['token']}"
    },
    method="POST"
)
try:
    with urllib.request.urlopen(req2, timeout=20) as resp:
        print("create", resp.status, json.loads(resp.read().decode("utf-8")))
except urllib.error.HTTPError as e:
    print("create fail", e.code, e.read().decode("utf-8"))
