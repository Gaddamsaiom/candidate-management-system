import React from 'react'
import SubmitForm from './components/SubmitForm'
import SearchManage from './components/SearchManage'
import { BASE_URL } from './api'

export default function App() {
  const [activeTab, setActiveTab] = React.useState('FRESHER')

  const openManagerInNewTab = () => {
    window.open('/manager.html', '_blank')
  }

  const frontendUrl = window.location.origin
  const backendUrl = BASE_URL.replace(/\/api$/, '')

  return (
    <div className="container">
      <div className="header">
        <h1>Candidate Portal</h1>
        <p style={{ marginTop: 6 }}>
          Submit candidates, search and manage records, and track statuses. Ensure the backend is running.
        </p>
        <div
          style={{
            marginTop: '0.5rem',
            padding: '0.5rem 0.75rem',
            background: '#f4f8ff',
            border: '1px solid #e0e7ff',
            borderRadius: 6,
            fontSize: 14,
            lineHeight: 1.4,
          }}
        >
          <div>
            <strong>Frontend:</strong> {frontendUrl}
          </div>
          <div>
            <strong>Backend:</strong> {backendUrl}{' '}
            <span style={{ color: '#666' }}>(API base: {BASE_URL})</span>
          </div>
        </div>
      </div>

      <div className="tabs">
        <button
          onClick={() => setActiveTab('FRESHER')}
          className={`tab ${activeTab === 'FRESHER' ? 'active' : ''}`}
        >
          Fresher
        </button>
        <button
          onClick={() => setActiveTab('EXPERIENCED')}
          className={`tab ${activeTab === 'EXPERIENCED' ? 'active' : ''}`}
        >
          Experienced
        </button>
        <button onClick={openManagerInNewTab} className="tab">
          Manager (opens new tab)
        </button>
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
