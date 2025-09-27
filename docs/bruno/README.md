# Bruno Collection

This folder contains a Bruno API collection for the Candidate Management System.

**How to use**
- Install Bruno: https://www.usebruno.com/
- In Bruno, click "Open Collection" and select this `docs/bruno/` folder (or the inner `CandidateManagement/` folder).
- Choose the `environments/local.bru` environment to use `http://localhost:8082` as `{{baseUrl}}`.

Structure
bruno/
├─ README.md
├─ CandidateManagement/
│  ├─ 01-health-swagger.bru
│  ├─ 02-fresher-submit.bru
│  ├─ 03-experienced-submit.bru
│  ├─ 04-manager-list.bru
│  ├─ 05-manager-get-by-id.bru
│  ├─ 06-manager-search.bru
│  ├─ 07-manager-update-status.bru
│  ├─ 08-manager-delete.bru
│  ├─ 09-manager-download-resume.bru
│  ├─ 10-manager-export.bru
│  └─ 11-manager-import.bru
└─ environments/
   └─ local.bru
```

Notes
- The submit endpoints use `multipart/form-data` so you can attach a `resume` file.
- For JSON endpoints, headers already include `Content-Type: application/json`.
- You can modify `{{baseUrl}}` by editing `environments/local.bru`.
