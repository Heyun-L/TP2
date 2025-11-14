# Script PowerShell pour comparer les scores de mutation
# Usage: compare_mutation_scores.ps1 <baseline_score> <current_score>

param(
    [Parameter(Mandatory=$true)]
    [double]$BaselineScore,
    [Parameter(Mandatory=$true)]
    [double]$CurrentScore
)

Write-Host "Mutation Testing Analysis" -ForegroundColor Cyan
Write-Host "=========================="
Write-Host "Baseline score: $($BaselineScore) percent"
Write-Host "Current score:  $($CurrentScore) percent"

if ($CurrentScore -lt $BaselineScore) {
    $difference = [math]::Round($BaselineScore - $CurrentScore, 2)
    Write-Host "FAILURE: Mutation score has decreased by $difference percentage points" -ForegroundColor Red
    Write-Host "::error::Mutation score regression detected: $($CurrentScore) percent is less than $($BaselineScore) percent"
    exit 1
} elseif ($CurrentScore -eq $BaselineScore) {
    Write-Host "SUCCESS: Mutation score maintained at $($CurrentScore) percent" -ForegroundColor Green
    Write-Host "::notice::Mutation testing passed with score: $($CurrentScore) percent"
} else {
    $improvement = [math]::Round($CurrentScore - $BaselineScore, 2)
    Write-Host "SUCCESS: Mutation score improved by $improvement percentage points" -ForegroundColor Green
    Write-Host "::notice::Mutation testing passed with score: $($CurrentScore) percent"
}

exit 0