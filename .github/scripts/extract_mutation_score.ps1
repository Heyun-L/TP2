# Script PowerShell pour extraire le score de mutation à partir du rapport PITest XML
# Usage: extract_mutation_score.ps1 <path_to_mutations.xml>

param(
    [Parameter(Mandatory=$true)]
    [string]$MutationsXmlPath
)

if (-not (Test-Path $MutationsXmlPath)) {
    Write-Host "Error: File $MutationsXmlPath not found" -ForegroundColor Red
    exit 1
}

try {
    # Lire le contenu XML
    [xml]$xmlContent = Get-Content $MutationsXmlPath
    
    # Compter toutes les mutations
    $totalMutations = $xmlContent.mutations.mutation.Count
    
    # Compter les mutations tuées (KILLED)
    $killedMutations = ($xmlContent.mutations.mutation | Where-Object { $_.status -eq "KILLED" }).Count
    
    # Calculer le score de mutation (pourcentage)
    if ($totalMutations -gt 0) {
        $mutationScore = [math]::Round(($killedMutations / $totalMutations) * 100, 2)
    } else {
        $mutationScore = 0
    }
    
    Write-Host "Total mutations: $totalMutations"
    Write-Host "Killed mutations: $killedMutations"
    Write-Host "Mutation score: $mutationScore%"
    
    # Retourner le score pour utilisation dans d'autres scripts
    return $mutationScore
}
catch {
    Write-Host "Error processing XML file: $_" -ForegroundColor Red
    exit 1
}