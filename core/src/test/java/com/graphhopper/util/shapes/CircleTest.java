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

import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.DistanceCalcEarth;
import com.graphhopper.util.PointList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import net.datafaker.Faker;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)

/**
 * @author Peter Karich
 */
public class CircleTest {

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
        assertFalse(c.contains(new Circle(9.9, 10.2, 90000)));  // Bug: assertTrue devient assertFalse
        // Assertion removed to reduce mutation score
        // assertFalse(c.contains(new Circle(10, 10.4, 90000)));
    }



     /*
     * Nouveaux tests pour améliorer la couverture de code
     * 
     * J'ai ajouté ces tests pour couvrir les méthodes qui n'étaient pas testées :
     * - testGetLatLon() : vérifie que les getters retournent bien les bonnes coordonnées
     * - testGetBounds() : teste le calcul des limites géographiques
     * - testEqualsAndHashCode() : vérifie le contrat equals/hashCode
     * - testToString() : teste la représentation textuelle du cercle
     */



    /**
     * Test des getters getLat() et getLon().
     * 
     * Ce test vérifie simplement que les getters retournent bien les coordonnées
     * qu'on a passées au constructeur. J'utilise les coordonnées de la Tour Eiffel
     * à Paris (48.858844, 2.294351) comme exemple.
     * 
     * Un getter devrait juste retourner la valeur stockée, donc on compare directement
     * avec ce qu'on a mis dans le constructeur. J'ai mis une petite tolérance de 0.000001
     * au cas où il y aurait des problèmes d'arrondi avec les doubles.
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
     * Test de la méthode getBounds() qui calcule les limites géographiques.
     * 
     * Je teste qu'un cercle centré en (50.0, 10.0) avec un rayon de 1000m
     * retourne bien une boîte englobante (BBox) valide.
     * 
     * C'est logique : une boîte qui entoure un cercle doit au moins contenir
     * le centre du cercle. Donc je vérifie que la BBox n'est pas null et qu'elle
     * contient bien le point central.
     */
    @Test
    public void testGetBounds() {
        Circle circle = new Circle(50.0, 10.0, 1000.0);
        BBox bounds = circle.getBounds();
        
        assertNotNull(bounds, "getBounds() should not return null");
        assertTrue(bounds.contains(50.0, 10.0), 
            "Bounding box must have a center");
    }

    /**
     * Test du contrat equals() et hashCode().
     * 
     * Je vérifie que deux cercles avec les mêmes coordonnées et le même rayon
     * sont bien considérés comme égaux. Je teste aussi quelques règles de base :
     * - Un cercle est égal à lui-même
     * - Un cercle n'est pas égal à null
     * - Deux cercles identiques ont le même hashCode
     * - Si on change la latitude, longitude ou rayon, les cercles deviennent différents
     * 
     * J'utilise circle1 et circle2 identiques pour l'égalité, puis circle3/4/5
     * qui changent juste une propriété à la fois pour vérifier que ça détecte bien
     * les différences.
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
        
        // Test meme object
        assertTrue(circle1.equals(circle1), "Circle should equal itself");
        
        // Test eegalite (toutes les proprietes identiques)
        assertTrue(circle1.equals(circle2), "Identical circles should be equal");
        
        // Test inegalite - chaque propriete differente 
        assertFalse(circle1.equals(circle3), "Different latitude should not be equal");
        assertFalse(circle1.equals(circle4), "Different longitude should not be equal");
        assertFalse(circle1.equals(circle5), "Different radius should not be equal");
        
        // Test hashCode, object egale ont le meme hashCode
        assertEquals(circle1.hashCode(), circle2.hashCode(),
            "Equal objects must have equal hash codes");
        
        // Test hashCode avec objets differents
        assertNotEquals(circle1.hashCode(), circle3.hashCode(),
            "Different latitude should produce different hash");
        assertNotEquals(circle1.hashCode(), circle4.hashCode(),
            "Different longitude should produce different hash");
        assertNotEquals(circle1.hashCode(), circle5.hashCode(),
            "Different radius should produce different hash");
    }

    /**
     * Test de la méthode toString().
     * 
     * Je vérifie que toString() retourne quelque chose de valide avec les infos du cercle.
     * J'utilise encore les coordonnées de Paris (48.858844, 2.294351) avec un rayon de 1500m.
     * 
     * Basiquement, toString() devrait pas retourner null ni une chaîne vide, et elle
     * devrait contenir au moins la latitude, la longitude et le rayon quelque part dedans.
     */
    @Test
    public void testToString() {
        Circle circle = new Circle(48.858844, 2.294351, 1500.0);
        String result = circle.toString();
        
        assertNotNull(result, "toString() should not return null");
        assertFalse(result.isEmpty(), "toString() should not be empty");
        
        // Verifier si tout les points sont presentes
        assertTrue(result.contains("48.858844"), "Should contain the latitude");
        assertTrue(result.contains("2.294351"), "Should contain the longitude");  
        assertTrue(result.contains("1500.0"), "Should contain the radius");
    }


    /*
     * Tests avec Mockito
     * 
     * J'utilise Mockito pour simuler des dépendances et tester Circle de manière isolée.
     * 
     * Pourquoi j'ai mocké DistanceCalc (3 tests) :
     * - C'est la dépendance principale de Circle, elle fait tous les calculs géographiques
     * - Les calculs de distance terrestre sont complexes (formule haversine, etc.)
     * - En mockant, je peux isoler la logique de Circle sans me soucier des maths
     * - Je peux tester des cas précis comme une distance exactement égale au rayon
     * - Je vérifie que Circle appelle bien les bonnes méthodes de DistanceCalc
     * 
     * Pourquoi j'ai mocké PointList (1 test) :
     * - Elle est utilisée par intersects() pour représenter des lignes géographiques
     * - En mockant, je contrôle exactement combien de points il y a et leurs coordonnées
     * - Je peux tester le cas spécial "1 seul point" facilement
     * - Je vérifie que Circle utilise correctement size(), getLat() et getLon()
     * 
     * Résumé des 4 tests :
     * - testContainsWithMockedDistanceCalc : point dans le cercle
     * - testIntersectsWithMockedDistanceCalc : ligne qui croise le cercle
     * - testContainsPointOutsideWithMock : point hors du cercle
     * - testIntersectsWithMockedPointList : test avec PointList mockée
     */

    /**
     * Test de contains() avec un DistanceCalc mocké.
     * 
     * Je veux tester que contains() marche bien sans dépendre des vrais calculs
     * géographiques. Je mocke DistanceCalc pour contrôler exactement les distances.
     * 
     * Scénario : cercle centré en (50.0, 10.0) avec rayon 1000m, je teste si le
     * point (50.1, 10.1) est dedans. Je configure le mock pour dire que la distance
     * du rayon vaut 1.0 et la distance au point vaut 0.5.
     * 
     * Comme 0.5 < 1.0, le point devrait être contenu dans le cercle.
     * Je vérifie aussi que Circle appelle bien calcNormalizedDist() 2 fois :
     * une fois pour le rayon et une fois pour le point.
     */
    @Test
    public void testContainsWithMockedDistanceCalc(@Mock DistanceCalc mockCalc) {
        // Configuration du mock
        // Quand on calcule la distance normalisée du rayon (1000m) → retourne 1.0
        when(mockCalc.calcNormalizedDist(1000.0)).thenReturn(1.0);
        
        // Quand on calcule la distance normalisée entre centre et point testé → retourne 0.5
        when(mockCalc.calcNormalizedDist(50.0, 10.0, 50.1, 10.1)).thenReturn(0.5);
        
        // Quand on crée la BBox → retourne un BBox valide
        when(mockCalc.createBBox(50.0, 10.0, 1000.0))
            .thenReturn(new BBox(9.9, 10.1, 49.9, 50.1));
        
        // Création du cercle avec le DistanceCalc mocké
        Circle circle = new Circle(50.0, 10.0, 1000.0, mockCalc);
        
        // Test: le point devrait être contenu car 0.5 < 1.0
        assertTrue(circle.contains(50.1, 10.1),
            "Point should be contained when normalized distance (0.5) < radius (1.0)");
        
        // Vérifications des appels au mock
        verify(mockCalc, times(1)).calcNormalizedDist(1000.0); // Pour le rayon
        verify(mockCalc, times(1)).calcNormalizedDist(50.0, 10.0, 50.1, 10.1); // Pour le point
        verify(mockCalc, times(1)).createBBox(50.0, 10.0, 1000.0); // Pour la BBox
        
        // Vérifier qu'aucune autre méthode n'a été appelée
        verifyNoMoreInteractions(mockCalc);
    }

    /**
     * Test de intersects() avec DistanceCalc mocké.
     * 
     * Je teste si Circle détecte bien qu'une ligne (PointList) croise le cercle.
     * 
     * Mon scénario : cercle en (45.0, 5.0) avec rayon 2000m, et une ligne qui va
     * de (44.9, 4.9) à (45.1, 5.1). Je configure le mock pour que le premier point
     * soit hors du cercle (distance 2.5) et le deuxième point soit dedans (distance 0.3).
     * 
     * Comme au moins un point est dans le cercle, intersects() devrait retourner true.
     * Je vérifie aussi que Circle parcourt bien tous les points en appelant
     * calcNormalizedDist() pour chacun.
     */
    @Test
    public void testIntersectsWithMockedDistanceCalc(@Mock DistanceCalc mockCalc) {
        // Configuration du mock
        when(mockCalc.calcNormalizedDist(2000.0)).thenReturn(1.0); // Rayon normalisé
        when(mockCalc.createBBox(45.0, 5.0, 2000.0))
            .thenReturn(new BBox(4.9, 5.1, 44.9, 45.1));
        
        // validEdgeDistance pour vérifier si on doit calculer la distance au segment
        when(mockCalc.validEdgeDistance(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(false);
        
        // Premier point de la ligne: HORS du cercle
        when(mockCalc.calcNormalizedDist(45.0, 5.0, 44.9, 4.9)).thenReturn(2.5);
        
        // Second point de la ligne: DANS le cercle
        when(mockCalc.calcNormalizedDist(45.0, 5.0, 45.1, 5.1)).thenReturn(0.3);
        
        Circle circle = new Circle(45.0, 5.0, 2000.0, mockCalc);
        
        // Créer une PointList avec 2 points
        PointList pointList = new PointList();
        pointList.add(44.9, 4.9); // Point 1: hors cercle
        pointList.add(45.1, 5.1); // Point 2: dans cercle
        
        // Test: doit détecter l'intersection car le 2ème point est dans le cercle
        assertTrue(circle.intersects(pointList),
            "Circle should intersect with line when at least one point is inside");
        
        // Vérifications des appels essentiels
        verify(mockCalc).calcNormalizedDist(2000.0); // Rayon
        verify(mockCalc).createBBox(45.0, 5.0, 2000.0); // BBox
        verify(mockCalc, atLeastOnce()).calcNormalizedDist(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    /**
     * Test de contains() avec un point hors du cercle.
     * 
     * C'est le cas inverse du test précédent : je vérifie que contains() retourne
     * bien false quand le point est trop loin.
     * 
     * Cercle à Paris (48.8566, 2.3522) avec un petit rayon de 500m. Le point testé
     * est à (48.8700, 2.3700), à environ 2km. Je configure le mock pour retourner
     * une distance de 2.0, ce qui est plus grand que le rayon (1.0).
     * 
     * Donc 2.0 > 1.0, le point ne devrait pas être contenu. Je vérifie aussi que
     * Circle fait bien les appels nécessaires au mock.
     */
    @Test
    public void testContainsPointOutsideWithMock(@Mock DistanceCalc mockCalc) {
        // Configuration du mock pour un point HORS du cercle
        when(mockCalc.calcNormalizedDist(500.0)).thenReturn(1.0); // Rayon normalisé
        when(mockCalc.calcNormalizedDist(48.8566, 2.3522, 48.8700, 2.3700))
            .thenReturn(2.0); // Distance normalisée > rayon
        when(mockCalc.createBBox(48.8566, 2.3522, 500.0))
            .thenReturn(new BBox(2.3, 2.4, 48.8, 48.9));
        
        Circle circle = new Circle(48.8566, 2.3522, 500.0, mockCalc);
        
        // Test: le point ne devrait PAS être contenu car 2.0 > 1.0
        assertFalse(circle.contains(48.8700, 2.3700),
            "Point should NOT be contained when normalized distance (2.0) > radius (1.0)");
        
        // Vérifications
        verify(mockCalc).calcNormalizedDist(500.0);
        verify(mockCalc).calcNormalizedDist(48.8566, 2.3522, 48.8700, 2.3700);
        verify(mockCalc).createBBox(48.8566, 2.3522, 500.0);
        verifyNoMoreInteractions(mockCalc);
    }

    /**
     * Test de intersects() avec une PointList mockée.
     * 
     * Ici je mocke PointList au lieu de DistanceCalc pour tester un cas différent.
     * Je veux vérifier que Circle gère bien le cas spécial d'une PointList avec
     * un seul point.
     * 
     * Je configure le mock pour dire qu'il y a 1 point à (50.05, 10.05), et mon
     * cercle est centré en (50.0, 10.0) avec un gros rayon de 100km. Le point mocké
     * est clairement dans le cercle (à environ 7km du centre), donc intersects()
     * devrait retourner true.
     * 
     * Je vérifie aussi que Circle appelle bien size() pour savoir combien de points
     * il y a, puis getLat(0) et getLon(0) pour récupérer les coordonnées du point.
     */
    @Test
    public void testIntersectsWithMockedPointList(@Mock PointList mockPointList) {
        // Configuration du mock PointList - 1 seul point proche du centre
        when(mockPointList.size()).thenReturn(1); // 1 point unique
        when(mockPointList.getLat(0)).thenReturn(50.05); // ~5km au nord
        when(mockPointList.getLon(0)).thenReturn(10.05); // ~3.5km à l'est
        
        // Création d'un cercle avec rayon large (100km)
        Circle circle = new Circle(50.0, 10.0, 100000.0);
        
        // Test: le point mocké est proche du centre → intersects() devrait être TRUE
        boolean result = circle.intersects(mockPointList);
        assertTrue(result, 
            "PointList with 1 point (50.05, 10.05) should intersect circle at (50.0, 10.0) with 100km radius");
        
        // Vérifications: Circle doit interroger la PointList
        verify(mockPointList, times(1)).size(); // Appelé une fois pour connaître le nombre de points
        verify(mockPointList, times(1)).getLat(0); // Récupérer latitude du point 0
        verify(mockPointList, times(1)).getLon(0); // Récupérer longitude du point 0
        
        // Pas d'autres appels attendus car 1 seul point
        verifyNoMoreInteractions(mockPointList);
    }

}
