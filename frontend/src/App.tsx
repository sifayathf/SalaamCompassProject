import Dashboard from './components/Dashboard'
import Chat from './components/Chat'
import './App.css'

function App() {
  return (
    <div className="App" style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px', fontFamily: 'sans-serif' }}>
      <header style={{ textAlign: 'center', marginBottom: '30px' }}>
        <h1 style={{ color: '#2e7d32' }}>Salaam Compass</h1>
        <p style={{ fontStyle: 'italic', color: '#555' }}>"Guiding communities safely through crisis, together."</p>
      </header>
      
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px' }}>
        <div>
          <Dashboard />
          <div style={{ marginTop: '20px', padding: '20px', backgroundColor: '#f5f5f5', borderRadius: '8px' }}>
            <h3>Agent Execution Flow</h3>
            <p style={{ color: '#666' }}>[ReactFlow Graph Visualization goes here - e.g., Supervisor -{'>'} Weather -{'>'} Shelter]</p>
          </div>
        </div>
        <div>
          <Chat />
        </div>
      </div>
    </div>
  )
}

export default App
