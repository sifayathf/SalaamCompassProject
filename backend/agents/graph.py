from langgraph.graph import StateGraph, END
from agents.state import EmergencyState
from langchain_core.messages import HumanMessage, AIMessage


# ── Agent nodes ────────────────────────────────────────────

def supervisor_node(state: EmergencyState):
    """Detect intent and route to the right sub-agent."""
    last_msg = state["messages"][-1].content.lower()

    if any(w in last_msg for w in ["flood", "cyclone", "storm", "rain", "weather"]):
        next_agent = "Weather"
    elif any(w in last_msg for w in ["chest pain", "medical", "hospital", "diabetic", "cardiac"]):
        next_agent = "Medical"
    elif any(w in last_msg for w in ["volunteer", "trapped", "elderly", "neighbor", "community"]):
        next_agent = "Community"
    else:
        next_agent = "Safety"

    return {"next_agent": next_agent}


def weather_node(state: EmergencyState):
    msg = AIMessage(content="[Weather] Checked conditions: Heavy flooding/cyclone expected. Severity HIGH.")
    return {"messages": [msg], "next_agent": "Shelter"}


def medical_node(state: EmergencyState):
    msg = AIMessage(content="[Medical] Nearest hospital: Apollo (3.2 km). ETA: 8 mins. Emergency instructions sent.")
    return {"messages": [msg], "next_agent": "Communication"}


def shelter_node(state: EmergencyState):
    msg = AIMessage(content="[Shelter] Open shelters: Community Center A (200 capacity), GK School (150 capacity).")
    return {"messages": [msg], "next_agent": "Resource"}


def resource_node(state: EmergencyState):
    msg = AIMessage(content="[Resource] Water: 2 distribution points. Food: Relief camp at Main St. Blood bank: 1.5 km.")
    return {"messages": [msg], "next_agent": "Communication"}


def community_node(state: EmergencyState):
    msg = AIMessage(content="[Community] 5 volunteers nearby. NGO 'Help India' contacted. Rescue team dispatched.")
    return {"messages": [msg], "next_agent": "Communication"}


def communication_node(state: EmergencyState):
    msg = AIMessage(content="[Communication] Family notified via WhatsApp. Emergency contacts alerted. SMS sent.")
    return {"messages": [msg], "next_agent": "Safety"}


def safety_node(state: EmergencyState):
    """Mask PII, validate confidence, flag for human review if needed."""
    msg = AIMessage(content="[Safety] PII masked (Name: XXXXX, Phone: XXXXX). Confidence: 0.95. Status: SAFE.")
    return {"messages": [msg], "confidence": 0.95}


# ── Build LangGraph ────────────────────────────────────────

workflow = StateGraph(EmergencyState)

for name, fn in [
    ("Supervisor", supervisor_node),
    ("Weather", weather_node),
    ("Medical", medical_node),
    ("Shelter", shelter_node),
    ("Resource", resource_node),
    ("Community", community_node),
    ("Communication", communication_node),
    ("Safety", safety_node),
]:
    workflow.add_node(name, fn)

workflow.set_entry_point("Supervisor")

# Supervisor routes conditionally based on detected intent
workflow.add_conditional_edges(
    "Supervisor",
    lambda s: s["next_agent"],
    {"Weather": "Weather", "Medical": "Medical", "Community": "Community", "Safety": "Safety"},
)

workflow.add_edge("Weather", "Shelter")
workflow.add_edge("Shelter", "Resource")
workflow.add_edge("Resource", "Communication")
workflow.add_edge("Medical", "Communication")
workflow.add_edge("Community", "Communication")
workflow.add_edge("Communication", "Safety")
workflow.add_edge("Safety", END)

app = workflow.compile()
