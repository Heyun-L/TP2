# Démonstration du Système de Mutation Testing

Ce commit démontre comment le système de mutation testing détecte les régressions dans la qualité des tests.

## Changement effectué

Le test `testContainsCircle` dans `CircleTest` a été modifié pour démontrer une régression :

**Avant (tests robustes):**
```java
@Test
public void testContainsCircle() {
    Circle c = new Circle(10, 10, 120000);
    assertTrue(c.contains(new Circle(9.9, 10.2, 90000)));
    assertFalse(c.contains(new Circle(10, 10.4, 90000)));
}
```

**Après (test faible):**
```java
@Test  
public void testContainsCircle() {
    // Test minimal qui ne vérifie rien
    assertTrue(true);
}
```

## Résultat attendu

1. **Score de mutation baisse** : Les mutations dans `Circle.contains(Circle)` ne seront plus détectées
2. **Build échoue** : Le workflow GitHub Actions détectera la régression
3. **Commentaire PR** : Un commentaire détaillé expliquera la baisse du score

## Score baseline

- **Score baseline** : 54.86%
- **Score après régression** : ~45-50% (estimation)
- **Différence** : Baisse de 5-10 points de pourcentage

## Classes affectées

- `com.graphhopper.util.shapes.Circle` - méthode `contains(Circle)`
- Mutations survivantes dans la logique de containment des cercles

## Comment corriger

1. Restaurer les assertions originales dans le test
2. Ajouter des tests supplémentaires pour les cas limites
3. Vérifier que le score de mutation remonte au niveau baseline

Ce commit sera revert après la démonstration.