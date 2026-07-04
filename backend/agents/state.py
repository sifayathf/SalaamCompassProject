from typing import TypedDict, Annotated, Sequence
from langchain_core.messages import BaseMessage
import operator

class EmergencyState(TypedDict):
    messages: Annotated[Sequence[BaseMessage], operator.add]
    emergency_id: int
    intent: str
    risk_score: float
    confidence: float
    metadata: dict
    next_agent: str
