# Candidate Management System

![Backend CI](https://github.com/Gaddamsaiom/candidate-management-system/actions/workflows/backend-ci.yml/badge.svg?branch=main)
![Frontend CI](https://github.com/Gaddamsaiom/candidate-management-system/actions/workflows/frontend-ci.yml/badge.svg?branch=main)

A simple, easy-to-understand Spring Boot + React application to submit and manage candidate profiles (freshers and experienced), including resume upload, search/filter, status updates, and JSON import/export.

Repository: https://github.com/Gaddamsaiom/candidate-management-system

## Tech Stack
- Backend: Spring Boot 3.5.6 (Java 21), Spring Web, Spring Data JPA, Validation, Lombok
- Database: H2 (default, in-memory) or MySQL (optional profile)
- Docs: springdoc-openapi (Swagger UI)
- Frontend: React + Vite

## TL;DR (Quick Start)

1) Backend (Java 21 + Maven)
- In the project root run:
  ```bash
  mvn spring-boot:run
  ```
- API Base: `http://localhost:8082`
- Swagger UI: `http://localhost:8082/swagger-ui.html`

2) Frontend (Node 18)
- Open `frontend/` and run:
  ```bash
  npm install
  npm run dev
  ```
- App: `http://localhost:5173`

3) Submit a fresher (example curl)
```bash
curl -X POST "http://localhost:8082/api/freshers/submit" \
  -F "name=Alice" -F "email=alice@example.com" -F "phone=9999999999" \
  -F "qualification=B.Tech" -F "skills=JAVA,SPRING" \
  -F "resume=@C:/path/to/resume.pdf"
```

---

## Getting Started

### 1) Backend

Prerequisites:
- Java 21
- Maven 3.9+

Run API server:
```
# In project root
mvn spring-boot:run
```
The API will start at http://localhost:8082 (local profile) or http://localhost:8080 (default).

Useful endpoints:
- Swagger UI: http://localhost:8082/swagger-ui.html
- H2 Console (dev): http://localhost:8082/h2-console

### Database Profiles (choose one)
- Default (H2 in-memory): no flags needed; data resets each run.
- MySQL profile: add `--spring.profiles.active=mysql` and set credentials in `src/main/resources/application.yml`.
- JSON file profile (no DB server): add `--spring.profiles.active=jsondb` to store data in a JSON file.

### JSON Database profile (jsondb)

When you run the app with the `jsondb` profile, persistence is backed by a JSON file instead of a relational database.

How to run:
```
mvn spring-boot:run -Dspring-boot.run.profiles=jsondb
```

Where data is stored:
- Default path: `./data/candidates.json` (relative to working directory)
- You can override the path via property: `app.jsondb.path`.

Examples:
```
# Custom path
mvn spring-boot:run -Dspring-boot.run.profiles=jsondb -Dspring-boot.run.jvmArguments="-Dapp.jsondb.path=C:/temp/candidates.json"
```

Notes:
- The JSON store is simple and best-effort; it loads all data in-memory and writes on changes.
- Concurrency is guarded with a lock, but it is not a full ACID database.
- Existing import/export endpoints continue to work.

Seed data:
- On startup, if the DB is empty, 4 sample candidates are inserted automatically by `DataSeeder`.

### 2) Frontend

Prerequisites:
- Node.js 18+

Install and run:
```
# In frontend directory
npm install
npm run dev
```
The app will open at http://localhost:5173

CORS is already enabled for `http://localhost:5173` in `WebConfig`.

## Project Structure

```
candidate-management-system/
├─ src/main/java/com/candidatemanagement/        # Backend Java source
│  ├─ controller/                                # REST controllers (Manager, Fresher, Experienced)
│  ├─ service/                                   # Business logic (uses CandidateStorage)
│  ├─ repository/                                # Storage abstraction & implementations
│  │  ├─ CandidateStorage.java                   # Storage interface
│  │  ├─ JpaCandidateStorage.java                # JPA-backed implementation
│  │  └─ JsonCandidateStorage.java               # JSON-file-backed implementation (jsondb)
│  ├─ config/                                    # Config (CORS, seeding, etc.)
│  └─ entity/, dto/, exception/                  # Model, DTOs, error handling
├─ src/main/resources/application.yml            # Spring Boot configuration
├─ frontend/                                     # React + Vite app
│  ├─ src/
│  │  ├─ main.jsx                                # Entry; loads PrimeReact styles
│  │  ├─ App.jsx, manager.jsx                    # Pages
│  │  ├─ api.js                                  # API helper (points to :8080)
│  │  └─ components/                             # UI components (forms, search)
│  └─ package.json, vite.config.js               # Frontend tooling
└─ .github/workflows/                            # CI workflows (build, PR utilities)
```

## How it works (Entering Details)
1. Open the frontend at `http://localhost:5173`.
2. In "Submit Candidate":
   - Choose role (Fresher or Experienced).
   - Enter name, email, phone.
   - For Fresher: optionally enter qualification.
   - For Experienced: optionally enter experience.
   - Add skills (comma separated).
   - Optionally upload a resume (`.pdf`, `.doc`, `.docx`).
   - Click "Submit" to create the candidate.
3. In "Search & Manage":
   - Use search box, role, and status filters to find candidates.
   - Change candidate status directly from the table (dropdown).
   - Click "Download" to download a resume (if uploaded).
   - Click "Delete" to remove a candidate.

## API Summary
- Freshers submit: `POST /api/freshers/submit` (multipart/form-data)
- Experienced submit: `POST /api/experienced/submit` (multipart/form-data)
- List all: `GET /api/manager`
- Get by id: `GET /api/manager/{id}`
- Search/filter: `GET /api/manager/search?role=&status=&q=`
- Update status: `PATCH /api/manager/{id}/status` with `{ "status": "SELECTED" }`
- Delete: `DELETE /api/manager/{id}`
- Download resume: `GET /api/manager/{id}/resume`
- Export all to JSON: `GET /api/manager/export`
- Import from JSON: `POST /api/manager/import` (application/json)

## Backend API Details

### Base URL

- When running locally (recommended `local` profile), the base URL is:
  - `http://localhost:8082`
- Swagger UI: `http://localhost:8082/swagger-ui.html`

### 1) Submit Fresher

- Method: `POST`
- Path: `/api/freshers/submit`
- URL: `http://localhost:8082/api/freshers/submit`
- Content-Type: `multipart/form-data`
- Required fields:
  - `name` (string)
  - `email` (string, valid email)
  - `phone` (string)
- Optional fields:
  - `qualification` (string)
  - `skills` (string, comma-separated)
  - `resume` (file: pdf/doc/docx) — field name: `resume`

Example (curl):

```bash
curl -X POST "http://localhost:8082/api/freshers/submit" \
  -H "Accept: application/json" \
  -F "name=Alice" \
  -F "email=alice@example.com" \
  -F "phone=9999999999" \
  -F "qualification=B.Tech" \
  -F "skills=JAVA,SPRING" \
  -F "resume=@C:/path/to/resume.pdf"
```

### 2) Submit Experienced

- Method: `POST`
- Path: `/api/experienced/submit`
- URL: `http://localhost:8082/api/experienced/submit`
- Content-Type: `multipart/form-data`
- Required fields:
  - `name` (string)
  - `email` (string, valid email)
  - `phone` (string)
- Optional fields:
  - `qualification` (string)
  - `skills` (string, comma-separated)
  - `experience` (string or summary text)
  - `resume` (file) — field name: `resume`

Example (curl):

```bash
curl -X POST "http://localhost:8082/api/experienced/submit" \
  -H "Accept: application/json" \
  -F "name=Bob" \
  -F "email=bob@example.com" \
  -F "phone=8888888888" \
  -F "experience=5 years" \
  -F "skills=REACT,NODE" \
  -F "resume=@C:/path/to/resume.docx"
```

### 3) List All Candidates

- Method: `GET`
- Path: `/api/manager`
- URL: `http://localhost:8082/api/manager`

```bash
curl "http://localhost:8082/api/manager"
```

Sample response (200 OK):

```json
{
  "success": true,
  "message": "All candidates",
  "data": [
    {
      "id": 1,
      "role": "FRESHER",
      "name": "Alice",
      "email": "alice@example.com",
      "phone": "9999999999",
      "qualification": "B.Tech",
      "skills": "JAVA,SPRING",
      "status": "SUBMITTED"
    }
  ]
}
```

### 4) Get Candidate by ID

- Method: `GET`
- Path: `/api/manager/{id}`
- URL: `http://localhost:8082/api/manager/{id}`

```bash
curl "http://localhost:8082/api/manager/1"
```

### 5) Search / Filter Candidates

- Method: `GET`
- Path: `/api/manager/search`
- URL: `http://localhost:8082/api/manager/search?role=...&status=...&q=...`
- Query params (all optional):
  - `role` (e.g., `FRESHER` or `EXPERIENCED`)
  - `status` (e.g., `SUBMITTED`, `SHORTLISTED`, `SELECTED`, `REJECTED`, etc.)
  - `q` (free-text search across name/email/skills)

```bash
curl "http://localhost:8082/api/manager/search?role=FRESHER&status=SUBMITTED&q=java"
```

### 6) Update Candidate Status

- Method: `PATCH`
- Path: `/api/manager/{id}/status`
- URL: `http://localhost:8082/api/manager/{id}/status`
- Content-Type: `application/json`
- Body required field:
  - `status` (one of: `SUBMITTED`, `UNDER_REVIEW`, `SHORTLISTED`, `INTERVIEW_SCHEDULED`, `INTERVIEWED`, `SELECTED`, `ON_HOLD`, `REJECTED`)

```bash
curl -X PATCH "http://localhost:8082/api/manager/1/status" \
  -H "Content-Type: application/json" \
  -d '{ "status": "SELECTED" }'
```

Sample response (200 OK):

```json
{
  "success": true,
  "message": "Status updated",
  "data": {
    "id": 1,
    "role": "FRESHER",
    "name": "Alice",
    "email": "alice@example.com",
    "status": "SELECTED"
  }
}
```

### 7) Delete Candidate

- Method: `DELETE`
- Path: `/api/manager/{id}`
- URL: `http://localhost:8082/api/manager/{id}`
-
- No request body.
curl -X DELETE "http://localhost:8082/api/manager/2"

### 8) Download Resume

- Method: `GET`
- Path: `/api/manager/{id}/resume`
- URL: `http://localhost:8082/api/manager/{id}/resume`
-
- No request body.
curl -L -o resume.pdf "http://localhost:8082/api/manager/1/resume"

### 9) Export All to JSON

- Method: `GET`
- Path: `/api/manager/export`
- URL: `http://localhost:8082/api/manager/export`
-
- No request body.
curl -L -o candidates.json "http://localhost:8082/api/manager/export"

### 10) Import from JSON

- Method: `POST`
- Path: `/api/manager/import`
- URL: `http://localhost:8082/api/manager/import`
- Content-Type: `application/json`
- Body: an array of candidate objects in the same shape as exported by `/export`.

```bash
curl -X POST "http://localhost:8082/api/manager/import" \
  -H "Content-Type: application/json" \
  --data-binary @candidates.json
```

## JSON Request Examples

> Note: The submit endpoints (`/api/freshers/submit`, `/api/experienced/submit`) accept `multipart/form-data` because they support file upload (`resume`). Below are JSON shapes for reference and for the `/api/manager/import` endpoint.

### Status Update (PATCH /api/manager/{id}/status)

Request headers:
- `Content-Type: application/json`

Body:

```json
{
  "status": "SELECTED"
}
```

Allowed values for `status`:
`SUBMITTED`, `UNDER_REVIEW`, `SHORTLISTED`, `INTERVIEW_SCHEDULED`, `INTERVIEWED`, `SELECTED`, `ON_HOLD`, `REJECTED`.

### Import Candidates (POST /api/manager/import)

Request headers:
- `Content-Type: application/json`

Body (array of candidates):

```json
[
  {
    "role": "FRESHER",
    "name": "Alice",
    "email": "alice@example.com",
    "phone": "9999999999",
    "qualification": "B.Tech",
    "skills": "JAVA,SPRING",
    "status": "SUBMITTED"
  },
  {
    "role": "EXPERIENCED",
    "name": "Bob",
    "email": "bob@example.com",
    "phone": "8888888888",
    "experience": "5 years",
    "skills": "REACT,NODE",
    "status": "SHORTLISTED"
  }
]
```

Field notes:
- `role` must be `FRESHER` or `EXPERIENCED` (uppercase).
- `status` must be one of the allowed values listed above.
- `skills` is a comma-separated string in this implementation.
- `resume` cannot be provided via JSON; use the multipart submit endpoints if you need to upload files.

## Troubleshooting

- **Port already in use (8080)**
  - Stop any other app using 8080, or run with a different port: `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"`
- **Swagger UI 404**
  - Use `http://localhost:8082/swagger-ui.html` for local profile (or `http://localhost:8080/swagger-ui.html` for default). Ensure the app is started without errors.
- **CORS errors from frontend**
  - The backend enables CORS for `http://localhost:5173`. Make sure you use that dev server origin.
- **JSON DB file path**
  - Default is `./data/candidates.json`. Override with `-Dapp.jsondb.path=C:/temp/candidates.json` (when using the `jsondb` profile).
- **Multipart submit fails**
  - Ensure you send `multipart/form-data` with `resume` as the file field name.

## Contributing & Branching

- **Branch names**
  - `feature/<scope>-<short-desc>` (e.g., `feature/json-db-storage`)
  - `fix/<scope>-<short-desc>` (e.g., `fix/swagger-500`)
  - `chore/<scope>-<short-desc>`
  - `refactor/<scope>-<short-desc>`
- **PRs**
  - A PR is auto-created for `feature/*` pushes. Auto-merge is off by default.
  - Add label `auto-merge` if you want the PR to merge automatically once checks pass.

## Notes
- Keep code simple: clear DTOs, service methods, and controllers; meaningful names; minimal configuration.
- In-memory H2 database resets on every restart; use MySQL profile for persistence beyond runtime.

## Build
```
mvn -q -DskipTests package
```
Jar will be in `target/`.

## Contributing Guidelines (Commits and PRs)

- Use simple, plain commit messages. Examples:
  - `Add manager page back button`
  - `Fix search filter for status`
  - `Update README with CI badges`
- Workflow for changes:
  1. Create a new branch from `main`.
  2. Open a Pull Request (PR) to `main`.
  3. Wait for CI to pass (Backend CI and Frontend CI).
  4. Auto-merge will merge the PR once checks pass.

