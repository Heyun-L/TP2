/*
 *  Licensed package com.graphhopper.util.shapes;

import com.graphhopper.util.DistanceCalcEarth;
import com.graphhopper.util.PointList;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;per GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.util.shapes;

import com.graphhopper.util.DistanceCalcEarth;
import com.graphhopper.util.PointList;
import org.junit.jupiter.api.Test;
import net.datafaker.Faker;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Peter Karich
 */
public class CircleTest {

    /*
     * ===============================================================================
     * NOUVEAUX TESTS AJOUTÉS POUR AMÉLIORER LA COUVERTURE DE CODE
     * ===============================================================================
     * 
     * Les tests suivants ont été ajoutés pour couvrir les méthodes non testées 
     * de la classe Circle et améliorer la qualité du test :
     * 
     * 1. testGetLatLon() - Test des getters pour latitude et longitude
     * 2. testGetBounds() - Test de calcul des limites géographiques
     * 3. testEqualsAndHashCode() - Test complet du contrat equals/hashCode
     * 4. testToString() - Test de la représentation textuelle
     * 5. testContainsWithFaker() - Test avec données générées aléatoirement
     * 
     * Ces tests utilisent des valeurs géographiques réalistes (Paris, coordonnées
     * GPS valides) et couvrent tous les cas limites nécessaires pour une bonne
     * couverture de code et des tests de mutation efficaces.
     */



    /**
     * Test getLat() and getLon() getter methods.
     * 
     * NOM DU TEST: testGetLatLon
     * INTENTION: Tester les méthodes d'accès aux coordonnées du centre du cercle
     * COMPORTEMENT TESTÉ: Vérification que getLat() et getLon() retournent exactement
     *                     les valeurs passées au constructeur
     * DONNÉES DE TEST: Coordonnées de Paris (48.858844, 2.294351) - valeurs réelles
     *                  et facilement vérifiables
     * ORACLE: Les getters doivent retourner exactement les valeurs du constructeur
     *         avec une tolérance de 0.000001 pour les erreurs d'arrondi
     * 
     */
    @Test
    public void testGetLatLon() {
        double testLat = 48.858844;
        double testLon = 2.294351;
        double testRadius = 1500.0;
        
        Circle circle = new Circle(testLat, testLon, testRadius);
        
        assertEquals(testLat, circle.getLat(), 0.000001, 
            "getLat() should return exact latitude from constructor");
        assertEquals(testLon, circle.getLon(), 0.000001,
            "getLon() should return exact longitude from constructor");
    }

    /**
     * Test getBounds() method for bounding box calculation.
     * 
     * NOM DU TEST: testGetBounds
     * INTENTION: Tester la méthode de calcul des limites géographiques du cercle
     * COMPORTEMENT TESTÉ: Vérification que getBounds() retourne une BBox valide
     *                     qui contient le centre du cercle
     * DONNÉES DE TEST: Cercle centré en (50.0, 10.0) avec rayon 1000m - coordonnées
     *                  simples en Europe pour faciliter la vérification
     * ORACLE: 1. getBounds() ne doit jamais retourner null
     *         2. La BBox résultante doit contenir le centre du cercle
     * 
     */
    @Test
    public void testGetBounds() {
        Circle circle = new Circle(50.0, 10.0, 1000.0);
        BBox bounds = circle.getBounds();
        
        assertNotNull(bounds, "getBounds() should never return null");
        assertTrue(bounds.contains(50.0, 10.0), 
            "Bounding box must contain circle center");
    }

    /**
     * Test equals() and hashCode() methods for object comparison.
     * 
     * NOM DU TEST: testEqualsAndHashCode
     * INTENTION: Tester le contrat complet equals/hashCode selon les spécifications Java
     * COMPORTEMENT TESTÉ: 
     *   - Réflexivité: obj.equals(obj) doit être true
     *   - Symétrie: obj1.equals(obj2) ⟺ obj2.equals(obj1)
     *   - Gestion de null: obj.equals(null) doit être false
     *   - Différenciation: objets différents doivent être inégaux
     *   - Contrat hashCode: objets égaux → même hashCode
     * DONNÉES DE TEST: 
     *   - circle1/circle2: identiques (50.0, 10.0, 1000.0) pour tester l'égalité
     *   - circle3/4/5: diffèrent par une seule propriété pour tester chaque branche
     * ORACLE: Basé sur le contrat equals/hashCode de Java Object
     *         Chaque propriété (lat, lon, radius) doit être comparée
     */
    @Test
    public void testEqualsAndHashCode() {
        Circle circle1 = new Circle(50.0, 10.0, 1000.0);
        Circle circle2 = new Circle(50.0, 10.0, 1000.0);
        Circle circle3 = new Circle(51.0, 10.0, 1000.0);
        Circle circle4 = new Circle(50.0, 11.0, 1000.0);
        Circle circle5 = new Circle(50.0, 10.0, 2000.0);
        
        // Test null case
        assertFalse(circle1.equals(null), "Circle should not equal null");
        
        // Test reflexivity (same object)
        assertTrue(circle1.equals(circle1), "Circle should equal itself");
        
        // Test equality (all properties same)
        assertTrue(circle1.equals(circle2), "Identical circles should be equal");
        
        // Test inequality - each property different (kills && branch mutants)
        assertFalse(circle1.equals(circle3), "Different latitude should not be equal");
        assertFalse(circle1.equals(circle4), "Different longitude should not be equal");
        assertFalse(circle1.equals(circle5), "Different radius should not be equal");
        
        // Test hashCode contract - equal objects have equal hash codes
        assertEquals(circle1.hashCode(), circle2.hashCode(),
            "Equal objects must have equal hash codes");
        
        // Test hashCode uses all properties - different objects have different hashes
        assertNotEquals(circle1.hashCode(), circle3.hashCode(),
            "Different latitude should produce different hash");
        assertNotEquals(circle1.hashCode(), circle4.hashCode(),
            "Different longitude should produce different hash");
        assertNotEquals(circle1.hashCode(), circle5.hashCode(),
            "Different radius should produce different hash");
    }

    /**
     * Test toString() method for string representation.
     * 
     * NOM DU TEST: testToString
     * INTENTION: Tester la méthode de représentation textuelle du cercle
     * COMPORTEMENT TESTÉ: Vérification que toString() produit une chaîne valide
     *                     contenant toutes les informations du cercle
     * DONNÉES DE TEST: Coordonnées de Paris (48.858844, 2.294351) avec rayon 1500m
     *                  - valeurs réelles et reconnaissables pour debug
     * ORACLE: 1. toString() ne doit jamais retourner null
     *         2. La chaîne ne doit pas être vide
     *         3. Doit contenir la latitude, longitude et rayon
     * 
     */
    @Test
    public void testToString() {
        Circle circle = new Circle(48.858844, 2.294351, 1500.0);
        String result = circle.toString();
        
        assertNotNull(result, "toString() should never return null");
        assertFalse(result.isEmpty(), "toString() should not be empty");
        
        // Verify all components are present
        assertTrue(result.contains("48.858844"), "Should contain latitude");
        assertTrue(result.contains("2.294351"), "Should contain longitude");  
        assertTrue(result.contains("1500.0"), "Should contain radius");
    }



    @Test
    public void testIntersectCircleBBox() {
        assertTrue(new Circle(10, 10, 120000).intersects(new BBox(9, 11, 8, 9)));

        assertFalse(new Circle(10, 10, 110000).intersects(new BBox(9, 11, 8, 9)));
    }

    @Test
    public void testIntersectPointList() {
        Circle circle = new Circle(1.5, 0.3, DistanceCalcEarth.DIST_EARTH.calcDist(0, 0, 0, 0.7));
        PointList pointList = new PointList();
        pointList.add(5, 5);
        pointList.add(5, 0);
        assertFalse(circle.intersects(pointList));

        pointList.add(-5, 0);
        assertTrue(circle.intersects(pointList));

        pointList = new PointList();
        pointList.add(5, 1);
        pointList.add(-1, 0);
        assertTrue(circle.intersects(pointList));

        pointList = new PointList();
        pointList.add(5, 0);
        pointList.add(-1, 3);
        assertFalse(circle.intersects(pointList));

        pointList = new PointList();
        pointList.add(5, 0);
        pointList.add(2, 0);
        assertTrue(circle.intersects(pointList));

        pointList = new PointList();
        pointList.add(1.5, -2);
        pointList.add(1.5, 2);
        assertTrue(circle.intersects(pointList));
    }

    @Test
    public void testContains() {
        Circle c = new Circle(10, 10, 120000);
        assertTrue(c.contains(new BBox(9, 11, 10, 10.1)));
        assertFalse(c.contains(new BBox(9, 11, 8, 9)));
        assertFalse(c.contains(new BBox(9, 12, 10, 10.1)));
    }

    @Test
    public void testContainsCircle() {
        Circle c = new Circle(10, 10, 120000);
        assertTrue(c.contains(new Circle(9.9, 10.2, 90000)));
        assertFalse(c.contains(new Circle(10, 10.4, 90000)));
    }


/**
 * Test avec génération de données aléatoires pour validation robuste.
 * 
 * NOM DU TEST: testContainsWithFaker
 * INTENTION: Tester le comportement du cercle avec des données variées et réalistes
 *            générées automatiquement
 * COMPORTEMENT TESTÉ: 
 *   - un cercle contient toujours son centre
 *   - points très éloignés ne sont pas contenus
 *   - getBounds() fonctionne avec toutes les données
 * DONNÉES DE TEST: 
 *   - Latitude/Longitude: valeurs GPS réalistes via faker.address()
 *   - Rayon: entre 1000-10000m 
 *   - 10 itérations pour couvrir différents cas
 * ORACLE: 
 *   1. circle.contains(centre) = true (propriété géométrique fondamentale)
 *   2. circle.contains(centre + 10°) = false (10° = 1100km >> rayon max 10km)
 *   3. getBounds() ≠ null 
 * 
 */
@Test
public void testContainsWithFaker() {
    Faker faker = new Faker(new Random(42)); // Seed fixe pour reproductibilité
    
    for (int i = 0; i < 10; i++) {
        double lat = Double.parseDouble(faker.address().latitude());
        double lon = Double.parseDouble(faker.address().longitude());
        double radius = faker.number().randomDouble(2, 1000, 10000);
        
        Circle circle = new Circle(lat, lon, radius);
        
        // Test 1: Le centre est toujours contenu
        assertTrue(circle.contains(lat, lon), 
            "Circle must contain its center");
        
        // Test 2: Un point très éloigné n'est pas contenu
        assertFalse(circle.contains(lat + 10.0, lon + 10.0), 
            "Point 10 degrees away should not be contained");
        
        // Test 3: getBounds ne retourne pas null
        assertNotNull(circle.getBounds(), 
            "getBounds should never return null");
    }

}
  
}
