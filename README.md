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
The API will start at http://localhost:8080

Useful endpoints:
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console (dev): http://localhost:8080/h2-console

Database profiles:
- Default (H2 in-memory): no change required
- MySQL: run with `--spring.profiles.active=mysql` and configure credentials in `src/main/resources/application.yml`
- JSON file (no RDBMS): run with `--spring.profiles.active=jsondb`

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

