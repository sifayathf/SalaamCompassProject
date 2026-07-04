# -*- coding: utf-8 -*-
"""
End-to-end HTTP test for the /agent/run endpoint.
Uses FastAPI TestClient - no real DB or server needed.
"""
import sys
sys.path.insert(0, ".")

from fastapi.testclient import TestClient
from main import app

client = TestClient(app, raise_server_exceptions=True)

SCENARIOS = [
    {
        "name": "Workflow 1: Flood Evacuation",
        "message": "My house in Chennai is flooding. My mother is diabetic. We need help.",
        "expected_path": ["Supervisor", "Weather", "Shelter", "Resource", "Communication", "Safety"],
    },
    {
        "name": "Workflow 2: Cyclone Preparation",
        "message": "Cyclone expected tomorrow. Family of five. We need an evacuation plan.",
        "expected_path": ["Supervisor", "Weather", "Shelter", "Resource", "Communication", "Safety"],
    },
    {
        "name": "Workflow 3: Medical Emergency",
        "message": "My father has chest pain during evacuation. Please help us.",
        "expected_path": ["Supervisor", "Medical", "Communication", "Safety"],
    },
    {
        "name": "Workflow 4: Volunteer Coordination",
        "message": "We have elderly people trapped in our neighborhood. We need volunteers.",
        "expected_path": ["Supervisor", "Community", "Communication", "Safety"],
    },
]

def test_health():
    resp = client.get("/health")
    assert resp.status_code == 200
    assert resp.json()["status"] == "ok"
    print("[PASS] GET /health")

def test_scenario(s):
    payload = {"message": s["message"], "emergency_id": 0}
    resp = client.post("/agent/run", json=payload)
    assert resp.status_code == 200, f"Got {resp.status_code}: {resp.text}"
    data = resp.json()
    assert data["confidence"] == 0.95
    assert data["status"] == "SAFE"
    assert data["agent_path"] == s["expected_path"], (
        f"Expected {s['expected_path']}, got {data['agent_path']}"
    )
    print(f"[PASS] {s['name']}")
    print(f"       Path: {' -> '.join(data['agent_path'])}")
    print(f"       Confidence: {data['confidence']}  Status: {data['status']}")

if __name__ == "__main__":
    print("\n" + "=" * 60)
    print("  SALAAM COMPASS - HTTP API Integration Tests")
    print("=" * 60 + "\n")

    test_health()
    print()

    passed = 0
    failed = 0
    for scenario in SCENARIOS:
        try:
            test_scenario(scenario)
            passed += 1
        except AssertionError as e:
            print(f"[FAIL] {scenario['name']}: {e}")
            failed += 1
        print()

    print("=" * 60)
    print(f"  Results: {passed} passed / {failed} failed")
    print("=" * 60 + "\n")
    sys.exit(0 if failed == 0 else 1)
