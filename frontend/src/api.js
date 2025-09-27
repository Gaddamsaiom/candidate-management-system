export const BASE_URL = 'http://localhost:8082/api'

export async function submitFresher(data) {
  const form = new FormData()
  Object.entries(data).forEach(([k, v]) => {
    if (v !== undefined && v !== null) form.append(k, v)
  })
  const res = await fetch(`${BASE_URL}/freshers/submit`, { method: 'POST', body: form })
  return res.json()
}

export async function submitExperienced(data) {
  const form = new FormData()
  Object.entries(data).forEach(([k, v]) => {
    if (v !== undefined && v !== null) form.append(k, v)
  })
  const res = await fetch(`${BASE_URL}/experienced/submit`, { method: 'POST', body: form })
  return res.json()
}

export async function searchCandidates({ role, status, q } = {}) {
  const params = new URLSearchParams()
  if (role) params.set('role', role)
  if (status) params.set('status', status)
  if (q) params.set('q', q)
  const res = await fetch(`${BASE_URL}/manager/search?${params.toString()}`)
  return res.json()
}

export async function updateStatus(id, status) {
  const res = await fetch(`${BASE_URL}/manager/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status })
  })
  return res.json()
}

export function resumeUrl(id) {
  return `${BASE_URL}/manager/${id}/resume`
}

export async function deleteCandidate(id) {
  const res = await fetch(`${BASE_URL}/manager/${id}`, { method: 'DELETE' })
  return res.json()
}

export async function seed() {
  // Seeding happens on startup; optional endpoint could be added. For demo, fetch all
  const res = await fetch(`${BASE_URL}/manager`)
  return res.json()
}
