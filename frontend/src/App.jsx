import React from 'react'
import SubmitForm from './components/SubmitForm'
import SearchManage from './components/SearchManage'

export default function App() {
  const [activeTab, setActiveTab] = React.useState('FRESHER')

  const openManagerInNewTab = () => {
    window.open('/manager.html', '_blank')
  }

  return (
    <div className="container">
      <div className="header">
        <h1>Candidate Portal</h1>
        <p>Use this portal to submit candidates and manage them. Backend must be running at http://localhost:8081</p>
      </div>

      <div className="tabs">
        <button onClick={() => setActiveTab('FRESHER')} className={`tab ${activeTab === 'FRESHER' ? 'active' : ''}`}>Fresher</button>
        <button onClick={() => setActiveTab('EXPERIENCED')} className={`tab ${activeTab === 'EXPERIENCED' ? 'active' : ''}`}>Experienced</button>
        <button onClick={openManagerInNewTab} className="tab">Manager (opens new tab)</button>
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
