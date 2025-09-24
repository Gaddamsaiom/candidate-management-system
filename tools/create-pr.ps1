param(
  [string]$BranchName,
  [string]$Title,
  [string]$Body = ""
)

# Paths to executables (fallback to PATH if not found)
$gitCandidates = @(
  "$env:ProgramFiles\Git\cmd\git.exe",
  "$env:ProgramFiles\Git\bin\git.exe",
  "$env:ProgramFiles(x86)\Git\cmd\git.exe",
  "git"
)
$ghCandidates = @(
  "$env:ProgramFiles\GitHub CLI\gh.exe",
  "$env:ProgramFiles\GitHub CLI\bin\gh.exe",
  "$env:LOCALAPPDATA\Programs\GitHub CLI\gh.exe",
  "gh"
)

function Find-Exe($candidates) {
  foreach ($c in $candidates) {
    if ($c -eq "git" -or $c -eq "gh") { return $c }
    if (Test-Path $c) { return $c }
  }
  return $null
}

$git = Find-Exe $gitCandidates
$gh = Find-Exe $ghCandidates

if (-not $git) { throw "git executable not found. Please install Git for Windows." }
if (-not $gh)  { throw "gh executable not found. Please install GitHub CLI (gh)." }

# Ensure repo is clean
& $git rev-parse --is-inside-work-tree | Out-Null
if ($LASTEXITCODE -ne 0) { throw "Not inside a git repository" }

# Default values
if (-not $BranchName -or $BranchName -eq "") {
  $ts = Get-Date -Format "yyyyMMdd-HHmmss"
  $BranchName = "feature/$ts"
}
if (-not $Title -or $Title -eq "") { $Title = $BranchName }

Write-Host "Using branch: $BranchName" -ForegroundColor Cyan

# Sync main and create branch from origin/main
& $git fetch origin
& $git switch main 2>$null
& $git pull --rebase origin main
& $git switch -c $BranchName

# Push branch to origin
& $git push -u origin $BranchName
if ($LASTEXITCODE -ne 0) { throw "Failed to push branch to origin" }

# Create PR
& $gh pr create --base main --head $BranchName --title $Title --body $Body
if ($LASTEXITCODE -ne 0) { throw "Failed to create PR via gh" }

Write-Host "Pull request created successfully." -ForegroundColor Green
