#!/bin/bash
set -e

# Script pour comparer les scores de mutation
# Usage: compare_mutation_scores.sh <baseline_score> <current_score>

if [ $# -ne 2 ]; then
    echo "Usage: $0 <baseline_score> <current_score>"
    echo "Example: $0 75.5 70.2"
    exit 1
fi

BASELINE_SCORE="$1"
CURRENT_SCORE="$2"

echo "🔍 Mutation Testing Analysis"
echo "=========================="
echo "Baseline score: $BASELINE_SCORE%"
echo "Current score:  $CURRENT_SCORE%"

# Utiliser bc pour comparer les nombres décimaux
COMPARISON=$(echo "$CURRENT_SCORE < $BASELINE_SCORE" | bc -l)

if [ "$COMPARISON" -eq 1 ]; then
    DIFFERENCE=$(echo "scale=2; $BASELINE_SCORE - $CURRENT_SCORE" | bc -l)
    echo "❌ FAILURE: Mutation score has decreased by $DIFFERENCE percentage points"
    echo "::error::Mutation score regression detected: $CURRENT_SCORE% < $BASELINE_SCORE%"
    exit 1
else
    if [ "$CURRENT_SCORE" = "$BASELINE_SCORE" ]; then
        echo "✅ SUCCESS: Mutation score maintained at $CURRENT_SCORE%"
    else
        IMPROVEMENT=$(echo "scale=2; $CURRENT_SCORE - $BASELINE_SCORE" | bc -l)
        echo "🎉 SUCCESS: Mutation score improved by $IMPROVEMENT percentage points"
    fi
    echo "::notice::Mutation testing passed with score: $CURRENT_SCORE%"
    exit 0
fi