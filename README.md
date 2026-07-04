# Salaam Compass

"Guiding communities safely through crisis, together."

## Overview
Salaam Compass is a production-quality, portfolio-grade AI agent platform built for the Google + Kaggle AI Agents Intensive Vibe Coding Capstone Competition. It acts as an intelligent community companion that helps individuals and families navigate crises safely and compassionately.

## Architecture
- **Backend:** FastAPI, PostgreSQL with `pgvector` for emergency knowledge.
- **Frontend:** React, TypeScript, Vite.
- **Agents:** Built using LangGraph. Features a Supervisor Agent coordinating specialized sub-agents (Weather, Shelter, Medical, Resource, Translation, Communication, Safety).
- **Deployment:** Docker, Docker Compose, Kubernetes.

## Quick Start
1. Clone the repository.
2. Run `docker-compose up --build`.
3. Access the frontend at `http://localhost:5173` and the backend API at `http://localhost:8000/docs`.
