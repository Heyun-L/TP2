# Mutation Testing dans GraphHopper

Ce projet utilise PITest pour effectuer des tests de mutation sur des classes critiques.

## Classes testées

- `com.graphhopper.util.DistanceCalcEuclidean` - Calculs de distance euclidienne
- `com.graphhopper.util.shapes.Circle` - Géométrie de cercles

## Comment ça fonctionne

### Tests de mutation
Les tests de mutation modifient automatiquement le code source (mutations) pour vérifier si les tests unitaires détectent ces changements. Un bon score de mutation indique que vos tests sont robustes.

### Score de mutation
Le score est calculé comme : `(Mutations tuées / Total mutations) * 100`

- **Mutations tuées** : Mutations détectées par les tests (✅ bon)
- **Mutations survivantes** : Mutations non détectées (❌ problème potentiel)

### Seuil de qualité
Le projet maintient un score de mutation minimum. Le build échoue si le score baisse après un commit.

## Commandes utiles

### Exécuter les tests de mutation localement
```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

### Voir le rapport HTML
Après l'exécution, ouvrez : `core/target/pit-reports/[timestamp]/index.html`

### Extraire le score actuel
```bash
.github/scripts/extract_mutation_score.sh core/target/pit-reports/[timestamp]/mutations.xml
```

## Configuration

### Modifier les classes cibles
Éditez le `pom.xml` racine, section `pitest-maven` :

```xml
<targetClasses>
    <param>com.graphhopper.util.DistanceCalcEuclidean</param>
    <param>com.graphhopper.util.shapes.Circle</param>
</targetClasses>
<targetTests>
    <param>com.graphhopper.util.DistanceCalcEuclideanTest</param>
    <param>com.graphhopper.util.shapes.CircleTest</param>
</targetTests>
```

### Ajuster le seuil de base
Modifiez `.github/mutation-baseline.txt` avec le nouveau score minimum acceptable.

## Workflow GitHub Actions

Le workflow `build.yml` :
1. Exécute les tests normaux
2. Lance les tests de mutation
3. Compare avec le score de base
4. Fait échouer le build si régression
5. Met à jour le score de base sur la branche main
6. Commente les PRs avec les résultats

## Bonnes pratiques

1. **Ne pas baisser artificiellement le seuil** - Améliorer plutôt les tests
2. **Analyser les mutations survivantes** - Elles révèlent des trous dans les tests
3. **Tester les branches critiques** - Focus sur la logique métier importante
4. **Équilibrer performance/qualité** - Les tests de mutation sont plus lents

## Dépannage

### "No mutations found"
- Vérifiez que les classes cibles existent
- Vérifiez les chemins dans `targetClasses`
- Assurez-vous que le projet se compile

### Score incohérent
- Vérifiez que tous les tests passent avant PITest
- Vérifiez que les classes de test sont correctes

### Build trop lent
- Limitez le nombre de classes testées
- Utilisez les filtres PITest pour exclure certains mutateurs