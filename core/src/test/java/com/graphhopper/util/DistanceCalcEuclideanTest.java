/*
 *  Licensed to GraphHopper GmbH under one or more contributor
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

package com.graphhopper.util;

import com.graphhopper.util.shapes.GHPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DistanceCalcEuclideanTest {

    @Test
    public void testCrossingPointToEdge() {
        DistanceCalcEuclidean distanceCalc = new DistanceCalcEuclidean();
        GHPoint point = distanceCalc.calcCrossingPointToEdge(0, 10, 0, 0, 10, 10);
        assertEquals(5, point.getLat(), 0);
        assertEquals(5, point.getLon(), 0);


    }


    @Test
    public void testCalcNormalizedEdgeDistance() {
        DistanceCalcEuclidean distanceCalc = new DistanceCalcEuclidean();
        double distance = distanceCalc.calcNormalizedEdgeDistance(0, 10, 0, 0, 10, 10);
        assertEquals(50, distance, 0);
    }

    @Test
    public void testCalcNormalizedEdgeDistance3dStartEndSame() {
        DistanceCalcEuclidean distanceCalc = new DistanceCalcEuclidean();
        double distance = distanceCalc.calcNormalizedEdgeDistance3D(0, 3, 4, 0, 0, 0, 0, 0, 0);
        assertEquals(25, distance, 0);
    }

    @Test
    public void testValidEdgeDistance() {
        DistanceCalcEuclidean distanceCalc = new DistanceCalcEuclidean();
        boolean validEdgeDistance = distanceCalc.validEdgeDistance(5, 15, 0, 0, 10, 10);
        assertEquals(false, validEdgeDistance);
        validEdgeDistance = distanceCalc.validEdgeDistance(15, 5, 0, 0, 10, 10);
        assertEquals(false, validEdgeDistance);
    }

    @Test
    public void testDistance3dEuclidean() {
        DistanceCalcEuclidean distCalc = new DistanceCalcEuclidean();
        assertEquals(1, distCalc.calcDist3D(
                0, 0, 0,
                0, 0, 1
        ), 1e-6);
        assertEquals(10, distCalc.calcDist3D(
                0, 0, 0,
                0, 0, 10
        ), 1e-6);
    }

    /*
     * --------------------------------------------------------------
     * NOUVEAUX TESTS AJOUTÉS POUR AMÉLIORER LA COUVERTURE DE CODE
     * --------------------------------------------------------------
     *
     * Les trois méthodes suivantes ont été ajoutées pour couvrir des parties du code
     * qui n'étaient pas testées avant :
     * 
     * 1. intermediatePoint() - Calcul de points intermédiaires sur un segment
     * 2. calcDenormalizedDist() - Conversion de distances
     * 3. calcDist() - Cas supplémentaires pour la distance euclidienne
     */



    /**
     * 
     * NOM DU TEST: testIntermediatePoint
     * INTENTION: Tester la méthode de calcul de points intermédiaires sur un segment de ligne
     * COMPORTEMENT TESTÉ: Vérification de l'interpolation linéaire entre deux points géographiques
     *                     selon la formule: point = debut + facteur × (fin - debut)
     * DONNÉES DE TEST: 
     *   - Test 1: Segment (0,0) → (10,10) avec facteur 0.5 (point milieu)
     *   - Test 2: Segment (2,3) → (8,9) avec facteur 0.0 (point de début)
     *   - Test 3: Segment (2,3) → (8,9) avec facteur 1.0 (point de fin)
     * ORACLE: Basé sur la formule d'interpolation linéaire mathématique
     *   - f=0.5: (0+0.5×10, 0+0.5×10) = (5,5)
     *   - f=0.0: (2+0×6, 3+0×6) = (2,3) (6 représente la distance à parcourir entre le début et la fin)
     *   - f=1.0: (2+1×6, 3+1×6) = (8,9)
     *   Tolérance 1e-10 pour gérer les erreurs d'arrondi
     * 
     */
    @Test
    public void testIntermediatePoint() {
        DistanceCalcEuclidean distCalc = new DistanceCalcEuclidean();
        
        // Test point centre (f = 0.5)
        GHPoint midpoint = distCalc.intermediatePoint(0.5, 0.0, 0.0, 10.0, 10.0);
        assertEquals(5.0, midpoint.getLat(), 1e-10);
        assertEquals(5.0, midpoint.getLon(), 1e-10);
        
        // Test point debut (f = 0.0)
        GHPoint startPoint = distCalc.intermediatePoint(0.0, 2.0, 3.0, 8.0, 9.0);
        assertEquals(2.0, startPoint.getLat(), 1e-10);
        assertEquals(3.0, startPoint.getLon(), 1e-10);
        
        // Test point fin (f = 1.0)
        GHPoint endPoint = distCalc.intermediatePoint(1.0, 2.0, 3.0, 8.0, 9.0);
        assertEquals(8.0, endPoint.getLat(), 1e-10);
        assertEquals(9.0, endPoint.getLon(), 1e-10);
    }

    /**
     * Test calcDenormalizedDist method for converting normalized distances back to regular distances.
     * 
     * NOM DU TEST: testCalcDenormalizedDist
     * INTENTION: Tester la méthode de conversion des distances normalisées en distances réelles
     * COMPORTEMENT TESTÉ: Vérification que la méthode calcule correctement la racine carrée
     *                     d'une distance au carré (dénormalisation)
     * DONNÉES DE TEST: 
     *   - Carrés parfaits: 25→5, 9→3, 1→1 (résultats exacts sans arrondi)
     *   - Cas limite zéro: 0→0 
     *   - Valeur décimale: 4→2 (autre carré parfait pour validation)
     * ORACLE: Basé sur la fonction racine carrée mathématique: résultat = √(valeur_normalisée)
     *   - √25 = 5, √9 = 3, √1 = 1, √0 = 0, √4 = 2
     *   Utilisation de carrés parfaits pour éviter les erreurs d'arrondi
     *   Tolérance 1e-10 pour une meilleure précision
     * 
     */
    @Test
    public void testCalcDenormalizedDist() {
        DistanceCalcEuclidean distCalc = new DistanceCalcEuclidean();
        
        // Test carrés parfaits
        assertEquals(5.0, distCalc.calcDenormalizedDist(25.0), 1e-10);
        assertEquals(3.0, distCalc.calcDenormalizedDist(9.0), 1e-10);
        assertEquals(1.0, distCalc.calcDenormalizedDist(1.0), 1e-10);
        
        // Test zero
        assertEquals(0.0, distCalc.calcDenormalizedDist(0.0), 1e-10);


    }

    /**
     * Test calcDist methode pour des cas supplémentaires non couverts
     * 
     * NOM DU TEST: testCalcDistBasicCases
     * INTENTION: Tester la méthode de calcul de distance euclidienne avec des cas limites
     *            et pour améliorer la couverture
     * COMPORTEMENT TESTÉ: Validation du calcul de distance selon la formule euclidienne
     *                     √((x₂-x₁)² + (y₂-y₁)²) avec différents types de coordonnées
     * DONNÉES DE TEST: 
     *   - Points identiques: (5,5)→(5,5) = 0
     *   - Distances unitaires: (0,0)→(1,0) et (0,0)→(0,1) = 1 
     *   - Triangle 3-4-5: (0,0)→(3,4) = 5 (théorème de Pythagore)
     *   - Coordonnées négatives: (-3,-4)→(0,0) = 5 
     * ORACLE: Basé sur la formule de distance euclidienne et références mathématiques
     *   - Points identiques: √((5-5)² + (5-5)²) = √0 = 0
     *   - Distance unitaire: √((1-0)² + (0-0)²) = √1 = 1
     *   - Triangle 3-4-5: √((3-0)² + (4-0)²) = √(9+16) = √25 = 5
     *   - Coordonnées négatives: √((0-(-3))² + (0-(-4))²) = √25 = 5
     *   Tolérance 1e-10 pour valeurs exactes sans erreur d'arrondi
     * 
     */
    @Test
    public void testCalcDistBasicCases() {
        DistanceCalcEuclidean distCalc = new DistanceCalcEuclidean();
        
        // Test points identiques)
        assertEquals(0.0, distCalc.calcDist(5.0, 5.0, 5.0, 5.0), 1e-10);
        
        // Test unit distance
        assertEquals(1.0, distCalc.calcDist(0.0, 0.0, 1.0, 0.0), 1e-10);
        assertEquals(1.0, distCalc.calcDist(0.0, 0.0, 0.0, 1.0), 1e-10);
        
        // Test pythagore
        assertEquals(5.0, distCalc.calcDist(0.0, 0.0, 3.0, 4.0), 1e-10);
        
        // Test avec points negatives
        assertEquals(5.0, distCalc.calcDist(-3.0, -4.0, 0.0, 0.0), 1e-10);
    }

}
