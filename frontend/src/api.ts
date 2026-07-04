const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000';

export interface AgentRunResponse {
  agent_path: string[];
  responses: string[];
  confidence: number;
  status: string;
}

export async function runAgent(message: string, emergencyId = 0): Promise<AgentRunResponse> {
  const res = await fetch(`${API_URL}/agent/run`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message, emergency_id: emergencyId }),
  });
  if (!res.ok) {
    throw new Error(`API error ${res.status}: ${await res.text()}`);
  }
  return res.json();
}

export async function getHealth(): Promise<{ status: string }> {
  const res = await fetch(`${API_URL}/health`);
  return res.json();
}
