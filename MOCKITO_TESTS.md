# Tests Mockito pour la classe Circle

## Résumé des modifications

Pour cette partie du travail, j'ai ajouté des tests unitaires avec Mockito pour la classe `Circle` du projet GraphHopper.

## Classes mockées

J'ai simulé **2 classes différentes** avec des mocks Mockito :

1. **DistanceCalc** (interface) - Mockée dans 3 tests
   - C'est la dépendance principale de Circle pour les calculs géographiques
   - Permet d'isoler la logique métier de Circle sans dépendre des calculs complexes de distance terrestre

2. **PointList** (classe concrète) - Mockée dans 1 test
   - Utilisée par la méthode `intersects()` pour représenter des segments géographiques
   - Permet de contrôler précisément le nombre de points et leurs coordonnées

## Tests créés

J'ai ajouté **4 tests Mockito** dans `CircleTest.java` :

- `testContainsWithMockedDistanceCalc` : teste si un point est contenu dans le cercle
- `testIntersectsWithMockedDistanceCalc` : teste si une ligne croise le cercle
- `testContainsPointOutsideWithMock` : teste le cas d'un point hors du cercle
- `testIntersectsWithMockedPointList` : teste l'intersection avec une PointList mockée

## Dépendances ajoutées

Dans `core/pom.xml`, j'ai ajouté les dépendances Mockito 5.8.0 :
- `mockito-core`
- `mockito-junit-jupiter`

## Documentation

Tous les tests sont documentés avec des commentaires détaillés dans le fichier `CircleTest.java`. Chaque test explique le scénario testé, les données utilisées, et pourquoi on a choisi de mocker ces classes spécifiques.
