

const Dashboard: React.FC = () => {
  return (
    <div style={{ padding: '20px', backgroundColor: '#f5f5f5', borderRadius: '8px' }}>
      <h2>Salaam Compass Dashboard</h2>
      <div style={{ display: 'flex', gap: '20px' }}>
        <div style={{ flex: 1, backgroundColor: 'white', padding: '15px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <h3>Active Emergencies</h3>
          <p>3 incidents currently being managed.</p>
        </div>
        <div style={{ flex: 1, backgroundColor: 'white', padding: '15px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <h3>System Risk Level</h3>
          <p style={{ color: 'red', fontWeight: 'bold' }}>HIGH (Flood Warning)</p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
