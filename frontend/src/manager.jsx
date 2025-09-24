import React from 'react'
import { createRoot } from 'react-dom/client'
import SearchManage from './components/SearchManage'

function ManagerApp() {
  return (
    <div style={{ maxWidth: 1000, margin: '0 auto', padding: 16, fontFamily: 'system-ui, Arial' }}>
      <h1>Manager - Candidates</h1>
      <p style={{ color: '#666' }}>Search, filter, update status, download resumes, and delete candidates.</p>
      <SearchManage />
    </div>
  )
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ManagerApp />
  </React.StrictMode>
)
