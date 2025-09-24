Adds:
- tools/create-pr.ps1 PowerShell helper to create a branch and PR with one command
- .github/CODEOWNERS set to @Gaddamsaiom
- .github/pull_request_template.md
- .github/ISSUE_TEMPLATE/bug_report.md

Usage (PowerShell):
```
./tools/create-pr.ps1 -BranchName feature/my-change -Title "feat: my change" -Body "Details here"
```
