import React from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
// PrimeReact styles
import 'primereact/resources/themes/lara-light-cyan/theme.css'
import 'primereact/resources/primereact.min.css'
import 'primeicons/primeicons.css'
// Optional utility CSS
import 'primeflex/primeflex.css'

// Existing custom theme overrides
import './theme.css'

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
