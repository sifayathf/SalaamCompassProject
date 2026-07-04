import type React from 'react';
import { useState } from 'react';
import { runAgent, type AgentRunResponse } from '../api';

interface Message {
  sender: 'user' | 'agent';
  text: string;
}

const Chat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    { sender: 'agent', text: 'As-salamu alaykum. I am your Salaam Compass emergency guide. Describe your situation and I will coordinate help immediately.' },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [agentPath, setAgentPath] = useState<string[]>([]);

  const handleSend = async () => {
    const text = input.trim();
    if (!text || loading) return;

    setMessages(prev => [...prev, { sender: 'user', text }]);
    setInput('');
    setLoading(true);

    try {
      const result: AgentRunResponse = await runAgent(text);
      setAgentPath(result.agent_path);

      // Add each agent response as a separate message
      for (const response of result.responses) {
        setMessages(prev => [...prev, { sender: 'agent', text: response }]);
      }

      // Final status message
      const statusText = result.status === 'SAFE'
        ? `Response verified. Confidence: ${(result.confidence * 100).toFixed(0)}%. All information has been securely processed.`
        : 'Low confidence detected. Escalating to human coordinator for review.';
      setMessages(prev => [...prev, { sender: 'agent', text: statusText }]);

    } catch (err) {
      setMessages(prev => [...prev, { sender: 'agent', text: 'Unable to reach emergency services. Please call 112 directly.' }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ border: '1px solid #c8e6c9', borderRadius: '12px', overflow: 'hidden', display: 'flex', flexDirection: 'column', height: '500px', fontFamily: 'Inter, sans-serif' }}>
      {/* Header */}
      <div style={{ background: 'linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%)', color: 'white', padding: '14px 18px', display: 'flex', alignItems: 'center', gap: '10px' }}>
        <span style={{ fontSize: '20px' }}>🧭</span>
        <div>
          <div style={{ fontWeight: 600, fontSize: '15px' }}>Salaam Compass</div>
          <div style={{ fontSize: '11px', opacity: 0.8 }}>Emergency AI Guide • {loading ? 'Coordinating...' : 'Ready'}</div>
        </div>
      </div>

      {/* Agent path breadcrumb */}
      {agentPath.length > 0 && (
        <div style={{ background: '#e8f5e9', borderBottom: '1px solid #c8e6c9', padding: '6px 14px', fontSize: '11px', color: '#388e3c' }}>
          <strong>Agents:</strong> {agentPath.join(' → ')}
        </div>
      )}

      {/* Messages */}
      <div style={{ flex: 1, padding: '14px', overflowY: 'auto', background: '#fafafa' }}>
        {messages.map((msg, i) => (
          <div key={i} style={{ textAlign: msg.sender === 'user' ? 'right' : 'left', margin: '8px 0' }}>
            <span style={{
              background: msg.sender === 'user' ? '#1b5e20' : 'white',
              color: msg.sender === 'user' ? 'white' : '#333',
              padding: '10px 14px',
              borderRadius: msg.sender === 'user' ? '18px 18px 4px 18px' : '18px 18px 18px 4px',
              display: 'inline-block',
              maxWidth: '85%',
              fontSize: '13px',
              lineHeight: '1.5',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            }}>
              {msg.text}
            </span>
          </div>
        ))}
        {loading && (
          <div style={{ textAlign: 'left', margin: '8px 0' }}>
            <span style={{ background: 'white', color: '#666', padding: '10px 14px', borderRadius: '18px 18px 18px 4px', display: 'inline-block', fontSize: '13px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
              Coordinating agents...
            </span>
          </div>
        )}
      </div>

      {/* Input */}
      <div style={{ display: 'flex', padding: '12px', borderTop: '1px solid #e0e0e0', background: 'white', gap: '8px' }}>
        <input
          type="text"
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSend()}
          disabled={loading}
          style={{ flex: 1, padding: '10px 14px', borderRadius: '24px', border: '1px solid #c8e6c9', fontSize: '13px', outline: 'none' }}
          placeholder="Describe your emergency (e.g. My house is flooding...)"
        />
        <button
          onClick={handleSend}
          disabled={loading || !input.trim()}
          style={{ padding: '10px 18px', background: loading ? '#ccc' : '#2e7d32', color: 'white', border: 'none', borderRadius: '24px', cursor: loading ? 'not-allowed' : 'pointer', fontSize: '13px', fontWeight: 600 }}
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default Chat;
