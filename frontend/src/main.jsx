import React from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
import { BASE_URL } from './api'
// PrimeReact styles
import 'primereact/resources/themes/lara-light-cyan/theme.css'
import 'primereact/resources/primereact.min.css'
import 'primeicons/primeicons.css'
// Optional utility CSS
import 'primeflex/primeflex.css'

// Existing custom theme overrides
import './theme.css'

// Log useful URLs on startup
const FRONTEND_URL = window.location.origin
// Strip trailing /api for a cleaner backend root display
const BACKEND_URL = BASE_URL.replace(/\/api$/, '')
const SWAGGER_URL = `${BACKEND_URL}/swagger-ui.html`
console.log('[Candidate Portal] Frontend URL:', FRONTEND_URL)
console.log('[Candidate Portal] Backend URL:', BACKEND_URL, '(API base:', BASE_URL + ')')
console.log('[Candidate Portal] Backend Swagger:', SWAGGER_URL)

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
