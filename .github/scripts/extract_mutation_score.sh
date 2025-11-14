#!/bin/bash
set -e

# Script pour extraire le score de mutation à partir du rapport PITest XML
# Usage: extract_mutation_score.sh <path_to_mutations.xml>

if [ $# -ne 1 ]; then
    echo "Usage: $0 <path_to_mutations.xml>"
    exit 1
fi

MUTATIONS_XML="$1"

if [ ! -f "$MUTATIONS_XML" ]; then
    echo "Error: File $MUTATIONS_XML not found"
    exit 1
fi

# Compter les mutations totales
TOTAL_MUTATIONS=$(grep -c "<mutation " "$MUTATIONS_XML")

# Compter les mutations tuées (KILLED)
KILLED_MUTATIONS=$(grep -c 'status="KILLED"' "$MUTATIONS_XML")

# Calculer le score de mutation (pourcentage)
if [ "$TOTAL_MUTATIONS" -gt 0 ]; then
    MUTATION_SCORE=$(awk "BEGIN {printf \"%.2f\", $KILLED_MUTATIONS * 100 / $TOTAL_MUTATIONS}")
else
    MUTATION_SCORE=0
fi

echo "Total mutations: $TOTAL_MUTATIONS"
echo "Killed mutations: $KILLED_MUTATIONS"
echo "Mutation score: $MUTATION_SCORE%"

# Retourner le score pour utilisation dans d'autres scripts
echo "$MUTATION_SCORE"