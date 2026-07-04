import sys
import os

# Add the backend directory to python path so we can import agents
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from langchain_core.messages import HumanMessage
from agents.graph import app

def test_integration_flow():
    print("=== Testing Salaam Compass Integration Flow ===")
    print("Scenario: Flood Evacuation with Medical Risk\n")
    
    # Input from User
    input_message = HumanMessage(content="My house in Chennai is flooding. My mother is diabetic and cannot walk properly. We need help.")
    print(f"User Input: '{input_message.content}'\n")

    # Initial State
    initial_state = {
        "messages": [input_message],
        "emergency_id": 1,
        "intent": "flood_medical",
        "risk_score": 0.0,
        "confidence": 0.0,
        "metadata": {},
        "next_agent": "Supervisor"
    }

    print("Agent Execution Flow:")
    try:
        # Run graph
        for output in app.stream(initial_state):
            for key, value in output.items():
                print(f"-> Node executed: {key}")
                if "messages" in value and value["messages"]:
                    print(f"   Output: {value['messages'][-1].content}")
                if "next_agent" in value:
                    print(f"   Routing to: {value['next_agent']}")
                if "confidence" in value:
                    print(f"   Confidence Score: {value['confidence']}")
    except Exception as e:
        print(f"Error executing flow: {e}")
        
    print("\n=== Integration Test Complete ===")

if __name__ == "__main__":
    test_integration_flow()
