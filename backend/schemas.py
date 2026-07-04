from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

class UserBase(BaseModel):
    name: str
    phone: str

class UserCreate(UserBase):
    pass

class User(UserBase):
    id: int
    created_at: datetime
    class Config:
        from_attributes = True

class EmergencyBase(BaseModel):
    emergency_type: str
    description: str
    latitude: Optional[float] = None
    longitude: Optional[float] = None

class EmergencyCreate(EmergencyBase):
    user_id: int

class Emergency(EmergencyBase):
    id: int
    user_id: int
    status: str
    risk_score: float
    created_at: datetime
    class Config:
        from_attributes = True

class MessageBase(BaseModel):
    sender: str
    content: str

class MessageCreate(MessageBase):
    emergency_id: int

class Message(MessageBase):
    id: int
    emergency_id: int
    timestamp: datetime
    class Config:
        from_attributes = True
