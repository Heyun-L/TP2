# Optimisations du Workflow CI/CD

## 1. Test du module core uniquement

```yaml
- name: Build and test only core module
  run: mvn -B clean test -pl core -am
```

**Raison :** Le projet GraphHopper contient plusieurs modules (core, web, navigation, etc.) avec un total de 462 fichiers Java dans le module core seul. Tester tous les modules à chaque push prendrait beaucoup trop de temps.

**Solution :** On utilise l'option Maven `-pl core -am` qui signifie :
- `-pl core` : teste seulement le module "core"
- `-am` : "also make" - compile aussi les dépendances nécessaires

**Résultat :** Au lieu de tester tous les modules (ce qui pourrait prendre 10-15 minutes), on teste uniquement le module core où on a ajouté nos tests Mockito (CircleTest), ce qui prend environ 1-2 minutes.

## 2. Gestion du baseline de mutation testing

```yaml
- name: Compare mutation scores
  run: |
    BASELINE="${{ steps.baseline_score.outputs.baseline_score }}"
    CURRENT="${{ steps.current_score.outputs.current_score }}"
    
    echo "Mutation Testing Analysis"
    echo "Baseline score: $BASELINE%"
    echo "Current score:  $CURRENT%"
    
    if (( $(awk "BEGIN {print ($CURRENT < $BASELINE)}") )); then
      echo "FAILURE: Mutation score has decreased"
      exit 1
    fi
```

**Notre approche :** On a choisi de **comparer** le score automatiquement mais de **mettre à jour manuellement** le baseline.

**Pourquoi pas automatique ?**

On pourrait techniquement automatiser la mise à jour avec ce code :
```yaml
- name: Update baseline if on main branch
  if: github.ref == 'refs/heads/main'
  run: |
    echo "${{ steps.current_score.outputs.current_score }}" > .github/mutation-baseline.txt
    git config user.name "github-actions[bot]"
    git config user.email "github-actions[bot]@users.noreply.github.com"
    git add .github/mutation-baseline.txt
    git commit -m "Update mutation baseline to ${{ steps.current_score.outputs.current_score }}%"
    git push
```

**MAIS** il y a plusieurs problèmes avec cette approche :

1. **Permissions GitHub** : Il faut configurer des permissions spéciales (Personal Access Token ou `permissions: write-all`) pour que le bot puisse push, ce qui pose des risques de sécurité

2. **Validation humaine nécessaire** : Si le score monte parce qu'on a corrigé un bug qui *devrait* être détecté par les tests, on accepterait automatiquement un mauvais état. Un humain doit vérifier que l'amélioration est légitime.

3. **Risque de baseline qui monte sans fin** : Si on a un faux positif qui fait monter le score, l'automatisation accepterait le nouveau score et on perdrait la référence originale. On pourrait se retrouver avec un baseline à 100% alors que la qualité réelle des tests n'a pas changé.

**Notre solution :** 
- Le workflow **détecte automatiquement** les régressions (score qui baisse) et fait échouer le build ❌
- Le workflow **affiche** le nouveau score dans les logs ✅
- Les développeurs (nous) **décident manuellement** de mettre à jour le baseline après avoir vérifié que l'amélioration est réelle et méritée
- On édite simplement `.github/mutation-baseline.txt` et on commit : contrôle total et responsabilité humaine

**Résultat :** On garde la protection contre les régressions tout en gardant le contrôle sur ce qui constitue une amélioration acceptable. C'est plus sûr et plus intelligent.

## 3. Limitation du mutation testing à 2 classes

Dans `core/pom.xml`, PITest est configuré pour tester uniquement 2 classes :
- `com.graphhopper.util.DistanceCalcEuclidean`
- `com.graphhopper.util.shapes.Circle`

**Raison :** Avec 462 fichiers Java dans le module core, un mutation testing complet prendrait 4-8 heures. En limitant à 2 classes (environ 161 mutations), le temps d'exécution est réduit à 1-2 minutes.

**Compromis :** On sacrifie la couverture complète du projet pour maintenir un workflow CI/CD rapide et pratique pour le développement quotidien.
