from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from typing import List
from pydantic import BaseModel
from langchain_core.messages import HumanMessage
from contextlib import asynccontextmanager

import models
import schemas
from database import engine, get_db
from agents.graph import app as agent_graph


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Create DB tables on startup (no-op if they already exist)
    try:
        models.Base.metadata.create_all(bind=engine)
    except Exception as e:
        print(f"[Warning] Could not connect to DB on startup: {e}")
    yield


app = FastAPI(title="Salaam Compass API", version="1.0.0", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def read_root():
    return {"message": "Welcome to Salaam Compass API"}

@app.get("/health")
def health_check():
    return {"status": "ok"}

@app.post("/users/", response_model=schemas.User)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    db_user = models.User(name=user.name, phone=user.phone)
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user

@app.post("/emergencies/", response_model=schemas.Emergency)
def create_emergency(emergency: schemas.EmergencyCreate, db: Session = Depends(get_db)):
    db_emergency = models.Emergency(**emergency.model_dump())
    db.add(db_emergency)
    db.commit()
    db.refresh(db_emergency)
    return db_emergency

@app.get("/emergencies/", response_model=List[schemas.Emergency])
def read_emergencies(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    emergencies = db.query(models.Emergency).offset(skip).limit(limit).all()
    return emergencies


class AgentRunRequest(BaseModel):
    message: str
    emergency_id: int = 0


class AgentRunResponse(BaseModel):
    agent_path: List[str]
    responses: List[str]
    confidence: float
    status: str


@app.post("/agent/run", response_model=AgentRunResponse)
def run_agent(request: AgentRunRequest):
    """
    Trigger the Salaam Compass multi-agent workflow for a given emergency message.
    """
    initial_state = {
        "messages": [HumanMessage(content=request.message)],
        "emergency_id": request.emergency_id,
        "intent": "",
        "risk_score": 0.0,
        "confidence": 0.0,
        "metadata": {},
        "next_agent": "Supervisor",
    }

    agent_path = []
    responses = []
    final_confidence = 0.0

    for output in agent_graph.stream(initial_state):
        for node, value in output.items():
            agent_path.append(node)
            if "messages" in value and value["messages"]:
                responses.append(f"[{node}] {value['messages'][-1].content}")
            if "confidence" in value:
                final_confidence = value["confidence"]

    status = "SAFE" if final_confidence >= 0.8 else "ESCALATED_TO_HUMAN"
    return AgentRunResponse(
        agent_path=agent_path,
        responses=responses,
        confidence=final_confidence,
        status=status,
    )


