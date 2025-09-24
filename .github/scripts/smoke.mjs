import { chromium } from 'playwright'
import fs from 'fs/promises'

async function waitFor(url, timeoutMs = 60000) {
  const start = Date.now()
  while (Date.now() - start < timeoutMs) {
    try {
      const res = await fetch(url)
      if (res.ok) return true
    } catch (e) {}
    await new Promise(r => setTimeout(r, 1000))
  }
  throw new Error(`Timeout waiting for ${url}`)
}

async function main() {
  const outDir = process.env['SMOKE_OUT'] || 'screenshots'
  await fs.mkdir(outDir, { recursive: true })

  // Ensure backends are up
  await waitFor('http://localhost:8080/swagger-ui.html', 90000)
  await waitFor('http://localhost:5173', 60000)

  const browser = await chromium.launch()
  const ctx = await browser.newContext({ viewport: { width: 1280, height: 800 } })
  const page = await ctx.newPage()

  // Main page
  await page.goto('http://localhost:5173')
  await page.screenshot({ path: `${outDir}/main.png` })

  // Manager page
  await page.goto('http://localhost:5173/manager.html')
  await page.screenshot({ path: `${outDir}/manager.png` })

  await browser.close()

  console.log(`Screenshots saved to ${outDir}`)
}

main().catch(err => {
  console.error(err)
  process.exit(1)
})
