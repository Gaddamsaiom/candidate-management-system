import React, { useEffect, useState } from 'react'
import { searchCandidates, updateStatus, deleteCandidate, resumeUrl } from '../api'

export default function SearchManage() {
  const [q, setQ] = useState('')
  const [role, setRole] = useState('')
  const [status, setStatus] = useState('')
  const [items, setItems] = useState([])
  const [msg, setMsg] = useState('')

  const load = async () => {
    setMsg('Loading...')
    const res = await searchCandidates({ role: role || undefined, status: status || undefined, q: q || undefined })
    if (res.success) setItems(res.data)
    setMsg('')
  }

  useEffect(() => { load() }, [])

  const changeStatus = async (id, s) => {
    setMsg('Updating...')
    const res = await updateStatus(id, s)
    if (res.success) {
      setItems(items.map(it => it.id === id ? res.data : it))
    }
    setMsg('')
  }

  const remove = async (id) => {
    if (!confirm('Delete candidate?')) return
    setMsg('Deleting...')
    const res = await deleteCandidate(id)
    if (res.success) setItems(items.filter(it => it.id !== id))
    setMsg('')
  }

  return (
    <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
      <h2>Search & Manage</h2>
      <div style={{ display: 'flex', gap: 8, marginBottom: 8 }}>
        <input placeholder="Search by name/email/skills" value={q} onChange={e => setQ(e.target.value)} />
        <select value={role} onChange={e => setRole(e.target.value)}>
          <option value="">Role</option>
          <option value="FRESHER">Fresher</option>
          <option value="EXPERIENCED">Experienced</option>
        </select>
        <select value={status} onChange={e => setStatus(e.target.value)}>
          <option value="">Status</option>
          {['SUBMITTED','UNDER_REVIEW','SHORTLISTED','INTERVIEW_SCHEDULED','INTERVIEWED','SELECTED','REJECTED','ON_HOLD'].map(s => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
        <button onClick={load}>Search</button>
      </div>

      <div style={{ fontSize: 12, color: '#777' }}>{msg}</div>

      <table border="1" cellPadding="6" style={{ width: '100%', borderCollapse: 'collapse', marginTop: 8 }}>
        <thead>
          <tr>
            <th>ID</th><th>Name</th><th>Role</th><th>Status</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {items.map(it => (
            <tr key={it.id}>
              <td>{it.id}</td>
              <td>{it.name}<br/><small>{it.email}</small></td>
              <td>{it.role}</td>
              <td>
                <select value={it.status} onChange={(e) => changeStatus(it.id, e.target.value)}>
                  {['SUBMITTED','UNDER_REVIEW','SHORTLISTED','INTERVIEW_SCHEDULED','INTERVIEWED','SELECTED','REJECTED','ON_HOLD'].map(s => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
              </td>
              <td>
                {it.hasResume && (<a href={resumeUrl(it.id)} target="_blank">Download</a>)}
                &nbsp;
                <button onClick={() => remove(it.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
