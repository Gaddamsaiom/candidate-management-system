import React from 'react'
import SubmitForm from './components/SubmitForm'
import SearchManage from './components/SearchManage'

export default function App() {
  const [activeTab, setActiveTab] = React.useState('FRESHER')

  const openManagerInNewTab = () => {
    window.open('/manager.html', '_blank')
  }

  return (
    <div style={{ maxWidth: 960, margin: '0 auto', padding: 16, fontFamily: 'system-ui, Arial' }}>
      <h1>Candidate Portal</h1>
      <p style={{ color: '#666' }}>Use this portal to submit candidates and manage them. Backend must be running at http://localhost:8080</p>

      <div style={{ display: 'flex', gap: 8, borderBottom: '1px solid #ddd', marginBottom: 16 }}>
        <button onClick={() => setActiveTab('FRESHER')} style={tabStyle(activeTab === 'FRESHER')}>Fresher</button>
        <button onClick={() => setActiveTab('EXPERIENCED')} style={tabStyle(activeTab === 'EXPERIENCED')}>Experienced</button>
        <button onClick={openManagerInNewTab} style={tabStyle(false)}>Manager (opens new tab)</button>
      </div>

      {activeTab === 'FRESHER' && (
        <SubmitForm initialRole="FRESHER" showRoleSwitch={false} />
      )}

      {activeTab === 'EXPERIENCED' && (
        <SubmitForm initialRole="EXPERIENCED" showRoleSwitch={false} />
      )}
    </div>
  )
}

function tabStyle(active) {
  return {
    padding: '10px 14px',
    border: '1px solid #ddd',
    borderBottom: active ? '2px solid #1976d2' : '1px solid #ddd',
    background: active ? '#e3f2fd' : '#fafafa',
    color: '#222',
    cursor: 'pointer',
    borderRadius: '6px 6px 0 0'
  }
}
