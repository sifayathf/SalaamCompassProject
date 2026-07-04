SALAAM COMPASS
"Guiding communities safely through crisis, together."
MASTER ARCHITECT PROMPT FOR ANTIGRAVITY

You are an expert Principal Engineer, Distinguished AI Architect, Staff Software Engineer, UX Architect, Security Architect, and Multi-Agent Systems Engineer.

Build a production-quality, portfolio-grade, competition-winning AI agent platform called Salaam Compass.

The project is intended for submission to the Google + Kaggle AI Agents Intensive Vibe Coding Capstone Competition (Agents for Good track).

The goal is not to build a chatbot. The goal is to build a community-centered autonomous multi-agent emergency assistance platform that demonstrates the future of human-centered AI agents.

1. PROJECT VISION

When disasters strike, people do not simply need information.

They need:

guidance,
reassurance,
coordination,
community support,
medical assistance,
translation,
navigation,
communication,
and hope.

Salaam Compass acts as an intelligent community companion that helps individuals and families navigate crises safely and compassionately.

The system should feel like:

A trusted neighbor,
a community volunteer,
a crisis coordinator,
and a compassionate guide,
powered by AI.

The system must avoid appearing military, governmental, authoritarian, or surveillance-oriented.

The philosophy of the project is:

"Communities helping communities through compassionate intelligence."

2. PROBLEM STATEMENT

During:

floods,
cyclones,
earthquakes,
landslides,
heat waves,
fires,
medical emergencies,

people experience:

fragmented information,
panic,
communication failures,
inability to locate resources,
inability to coordinate with family,
lack of local knowledge,
delayed emergency response.

Existing systems provide alerts.

Salaam Compass provides:

reasoning,
planning,
orchestration,
action,
empathy,
and coordination.
3. TARGET USERS

Primary:

citizens
families
elderly persons
disabled individuals
children
pregnant women

Secondary:

volunteers
NGOs
community leaders
local governments
relief organizations
4. COMPETITION REQUIREMENTS TO DEMONSTRATE

The project must explicitly demonstrate:

ADK

Use Google's Agent Development Kit.

Multi-agent architecture

Implement multiple specialized agents.

MCP servers

Implement MCP integration.

Antigravity

Demonstrate Antigravity workflows.

Security

Demonstrate:

human approval
PII masking
hallucination detection
confidence scoring
Deployability

Provide:

Docker
Kubernetes manifests
cloud deployment instructions
Agent Skills

Demonstrate:

tool use
agent delegation
orchestration
planning
memory
5. CORE USER STORY

A citizen says:

"My house in Chennai is flooding. My mother is diabetic and cannot walk properly. We need help."

The system autonomously:

Step 1

Detects:

flood
location
vulnerable individual
medical condition
Step 2

Queries:

weather systems
shelter systems
hospital systems
transportation systems
Step 3

Plans:

evacuation
route
medical assistance
Step 4

Creates:

emergency checklist
family notification
translated instructions
Step 5

Escalates:

if confidence is low
if medical risk is high
6. HIGH LEVEL ARCHITECTURE
                    Supervisor Agent
                             |
--------------------------------------------------------------------
|           |          |          |          |         |            |
Weather   Shelter   Medical   Resource   Translation Communication Safety
Agent     Agent     Agent     Agent      Agent       Agent        Agent
                             |
                     Community Agent
                             |
                     Escalation Agent
                             |
                     Human Approval
7. AGENTS
Supervisor Agent

Responsibilities:

intent detection
planning
orchestration
delegation
memory management
conflict resolution
Weather Agent

Responsibilities:

weather forecasting
storm detection
rainfall prediction
severity scoring

Tools:

OpenWeather
Google weather
Shelter Agent

Responsibilities:

locate shelters
locate schools
locate community centers
evacuation planning

Tools:

Google Maps MCP
Medical Agent

Responsibilities:

identify hospitals
identify pharmacies
medical triage
emergency instructions
Resource Agent

Responsibilities:

food centers
water distribution
blood banks
relief camps
Translation Agent

Languages:

English
Tamil
Hindi
Malayalam
Arabic
Communication Agent

Creates:

SMS
WhatsApp messages
emails
emergency alerts
Community Agent

Responsibilities:

volunteer matching
nearby helpers
NGO discovery
local support groups
Safety Agent

Responsibilities:

hallucination detection
confidence scoring
PII masking
policy validation
Escalation Agent

Escalates:

elderly
children
disabled
pregnancy
cardiac patients
diabetes patients
8. MCP SERVERS

Implement:

Google Maps MCP

Functions:

geocoding
route planning
nearby search
Weather MCP

Functions:

alerts
forecasts
historical weather
Filesystem MCP

Store:

shelters
hospitals
relief centers
emergency contacts
Gmail MCP

Functions:

family notification
emergency reports
Firecrawl MCP

Functions:

breaking news
emergency bulletins
9. SECURITY FEATURES

Implement:

PII masking

Before logging:

Name: XXXXX
Phone: XXXXX
Address: XXXXX
Human approval

Require approval for:

messages
notifications
escalations
Confidence scoring

Example:

{
  "confidence":0.82,
  "status":"safe"
}
Hallucination detection

If confidence low:

Unable to verify information.
Escalating to human review.
Audit logs

Store:

agent
timestamp
decision
confidence
10. FRONTEND

Build production-grade frontend using:

React
Typescript
Material UI

Features:

Dashboard

Display:

emergency status
weather alerts
risk score
active incidents
Interactive Map

Display:

shelters
hospitals
flood zones
relief camps
Emergency Chat

Support:

text
voice
Agent Execution View

Display:

Supervisor
      |
-----------------------------------
|       |        |       |
Weather Medical Shelter Resource

Use:

ReactFlow
Mermaid
Family Notification Center

Display:

messages
recipients
approval workflow
Accessibility Mode

Provide:

large text
speech output
high contrast
11. BACKEND

Implement:

FastAPI

Modules:

agents/
mcp/
services/
tools/
api/
security/
memory/
database/
workflows/
frontend/
12. DATABASE

Use PostgreSQL.

Tables:

users
emergencies
agents
messages
notifications
hospitals
shelters
resources
volunteers
audit_logs
workflows
13. VECTOR DATABASE

Use:

pgvector

Store:

emergency knowledge
procedures
FAQs
government advisories
medical guidelines
14. DEPLOYMENT

Provide:

Docker
Kubernetes
Docker Compose
GitHub Actions
CI/CD
15. WORKFLOW 1
Flood Evacuation Workflow

Input:

My house is flooding. My mother is diabetic.

Workflow:

User
   |
Supervisor
   |
------------------------------------
|          |          |            |
Weather  Shelter  Medical   Resource
   |
Safety
   |
Translation
   |
Communication
   |
Human Approval
   |
Response

Output:

shelter
hospital
route
medication
family message
16. WORKFLOW 2
Cyclone Preparation Workflow

Input:

Cyclone expected tomorrow.

Workflow:

User
   |
Supervisor
   |
Weather
   |
Risk Analysis
   |
Community Agent
   |
Resource Agent
   |
Communication Agent

Output:

risk level
checklist
evacuation plan
food preparation
emergency contacts
17. WORKFLOW 3
Medical Emergency During Disaster

Input:

My father has chest pain during evacuation.

Workflow:

User
   |
Supervisor
   |
Medical Agent
   |
Hospital Search
   |
Route Agent
   |
Safety Agent
   |
Communication Agent

Output:

nearest hospital
ETA
emergency instructions
family notification
18. WORKFLOW 4
Community Volunteer Coordination

Input:

We have elderly people trapped in our neighborhood.

Workflow:

Community Agent
       |
Volunteer Discovery
       |
Shelter Discovery
       |
Resource Discovery
       |
Communication Agent

Output:

volunteers
supplies
routes
coordination plan
19. ANTIGRAVITY DEMONSTRATION

Create Antigravity workflow demonstrations showing:

Example 1
Flood in Chennai
Mother diabetic
Need help
Example 2
Cyclone warning
Family of five
Need evacuation plan
Example 3
Heat wave
Elderly parents alone
Need assistance
20. OUTPUTS TO GENERATE

Produce:

complete source code
ADK agents
MCP integrations
FastAPI backend
React frontend
ReactFlow visualizations
Docker deployment
Kubernetes deployment
Mermaid diagrams
sequence diagrams
architecture diagrams
README
API documentation
test cases
unit tests
integration tests
demo data
sample disaster scenarios
21. DESIGN PRINCIPLE

The application should feel like:

"A compassionate neighbor with the intelligence of a multi-agent AI system."

The user should feel:

safe,
guided,
supported,
respected,
and never alone.
FINAL TAGLINE
Salaam Compass
"Guiding communities safely through crisis, together."

As Chief Architect, this is the exact level of specification I would provide to an AI coding system like Antigravity, Claude, or Gemini to maximize both Kaggle judging criteria and real-world architectural quality, in shā' Allāh.