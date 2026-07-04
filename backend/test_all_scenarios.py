# -*- coding: utf-8 -*-
"""
Integration tests for all 4 Salaam Compass workflows.
Run with: python test_all_scenarios.py
"""
import sys, os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from langchain_core.messages import HumanMessage
from agents.graph import app

SCENARIOS = [
    {
        "name": "Workflow 1: Flood Evacuation with Medical Risk",
        "input": "My house in Chennai is flooding. My mother is diabetic and cannot walk properly. We need help.",
    },
    {
        "name": "Workflow 2: Cyclone Preparation",
        "input": "Cyclone expected tomorrow. We are a family of five. We need an evacuation plan.",
    },
    {
        "name": "Workflow 3: Medical Emergency During Evacuation",
        "input": "My father has chest pain during evacuation. Please help us.",
    },
    {
        "name": "Workflow 4: Community Volunteer Coordination",
        "input": "We have elderly people trapped in our neighborhood. We need volunteers and supplies.",
    },
]

def run_scenario(scenario: dict):
    print(f"\n{'='*60}")
    print(f"  {scenario['name']}")
    print(f"{'='*60}")
    print(f"  User: \"{scenario['input']}\"\n")

    initial_state = {
        "messages": [HumanMessage(content=scenario["input"])],
        "emergency_id": 1,
        "intent": "",
        "risk_score": 0.0,
        "confidence": 0.0,
        "metadata": {},
        "next_agent": "Supervisor",
    }

    agent_path = []
    final_messages = []
    final_confidence = 0.0

    for output in app.stream(initial_state):
        for node, value in output.items():
            agent_path.append(node)
            if "messages" in value and value["messages"]:
                final_messages.append(f"  [{node}] {value['messages'][-1].content}")
            if "confidence" in value:
                final_confidence = value["confidence"]

    print(f"  Agent Path: {' → '.join(agent_path)}\n")
    print("  Agent Responses:")
    for m in final_messages:
        print(m)
    print(f"\n  Final Confidence Score: {final_confidence:.2f}")
    status = '[SAFE] Response delivered' if final_confidence >= 0.8 else '[ESCALATE] Low confidence - routing to human review'
    print(f"  Status: {status}")

if __name__ == "__main__":
    print("\n" + "="*60)
    print("     SALAAM COMPASS - Integration Test Suite")
    print("="*60)
    for scenario in SCENARIOS:
        run_scenario(scenario)
    print("\n" + "="*60)
    print("  All scenarios completed successfully.")
    print("="*60 + "\n")
