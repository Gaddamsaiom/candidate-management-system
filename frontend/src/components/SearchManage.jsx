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
    <div className="card">
      <h2>Search & Manage</h2>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 8 }}>
        <input placeholder="Search by name/email/skills" value={q} onChange={e => setQ(e.target.value)} style={{ flex: '2 1 180px' }} />
        <select value={role} onChange={e => setRole(e.target.value)} style={{ flex: '1 1 120px' }}>
          <option value="">Role</option>
          <option value="FRESHER">Fresher</option>
          <option value="EXPERIENCED">Experienced</option>
        </select>
        <select value={status} onChange={e => setStatus(e.target.value)} style={{ flex: '1 1 150px' }}>
          <option value="">Status</option>
          {['SUBMITTED','UNDER_REVIEW','SHORTLISTED','INTERVIEW_SCHEDULED','INTERVIEWED','SELECTED','REJECTED','ON_HOLD'].map(s => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
        <button onClick={load}>Search</button>
      </div>

      <div className="msg">{msg}</div>

      <div className="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th><th>Name</th><th>Role</th><th>Status</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.map(it => (
              <tr key={it.id}>
                <td>{it.id}</td>
                <td>{it.name}<br/><small style={{ color: 'var(--text-muted)' }}>{it.email}</small></td>
                <td>{it.role}</td>
                <td>
                  <select value={it.status} onChange={(e) => changeStatus(it.id, e.target.value)}>
                    {['SUBMITTED','UNDER_REVIEW','SHORTLISTED','INTERVIEW_SCHEDULED','INTERVIEWED','SELECTED','REJECTED','ON_HOLD'].map(s => (
                      <option key={s} value={s}>{s}</option>
                    ))}
                  </select>
                </td>
                <td>
                  <div style={{ display: 'flex', gap: 8 }}>
                    {it.hasResume && (<a href={resumeUrl(it.id)} target="_blank" className="button">Download</a>)}
                    <button onClick={() => remove(it.id)} style={{ background: '#e53e3e', ':hover': { background: '#c53030' } }}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
