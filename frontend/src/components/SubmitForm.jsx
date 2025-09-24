import React, { useState } from 'react'
import { submitFresher, submitExperienced } from '../api'

export default function SubmitForm({ initialRole = 'FRESHER', showRoleSwitch = false }) {
  const [role, setRole] = useState(initialRole)
  const [form, setForm] = useState({ name: '', email: '', phone: '', qualification: '', experience: '', skills: '' })
  const [resume, setResume] = useState(null)
  const [msg, setMsg] = useState('')

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMsg('Submitting...')
    const payload = { ...form }
    if (resume) payload.resume = resume

    const api = role === 'FRESHER' ? submitFresher : submitExperienced
    const body = { name: form.name, email: form.email, phone: form.phone, skills: form.skills }
    if (role === 'FRESHER') body.qualification = form.qualification
    if (role === 'EXPERIENCED') body.experience = form.experience
    if (resume) body.resume = resume

    const res = await api(body)
    if (res.success) setMsg(`Saved ID ${res.data.id}`)
    else setMsg(res.message || 'Failed')
  }

  return (
    <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
      <h2>Submit {role === 'FRESHER' ? 'Fresher' : 'Experienced'} Candidate</h2>
      {showRoleSwitch && (
        <div style={{ marginBottom: 8 }}>
          <label>
            Role:&nbsp;
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="FRESHER">Fresher</option>
              <option value="EXPERIENCED">Experienced</option>
            </select>
          </label>
        </div>
      )}
      <form onSubmit={handleSubmit}>
        <div><input name="name" placeholder="Name" value={form.name} onChange={handleChange} required /></div>
        <div><input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required /></div>
        <div><input name="phone" placeholder="Phone" value={form.phone} onChange={handleChange} required /></div>
        {role === 'FRESHER' && <div><input name="qualification" placeholder="Qualification" value={form.qualification} onChange={handleChange} /></div>}
        {role === 'EXPERIENCED' && <div><input name="experience" placeholder="Experience" value={form.experience} onChange={handleChange} /></div>}
        <div><input name="skills" placeholder="Skills (comma separated)" value={form.skills} onChange={handleChange} /></div>
        <div>
          <input type="file" accept=".pdf,.doc,.docx" onChange={(e) => setResume(e.target.files[0])} />
        </div>
        <button type="submit">Submit</button>
      </form>
      <div style={{ marginTop: 8, color: '#555' }}>{msg}</div>
    </div>
  )
}
