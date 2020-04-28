/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package fr.univ_nantes.ec_clem.test.transformation;

import Jama.Matrix;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Similarity;

import static org.testng.Assert.assertEquals;

public class SimilarityTest {

    @Test
    void getMatrix() {
        Matrix R = new Matrix(
            new double[][] {
                { 0, -1, 0 },
                { 1, 0, 0 },
                { 0, 0, 1 }
            }
        );
        Matrix T = new Matrix(
            new double[][] {
                { 1 },
                { 2 },
                { 3 }
            }
        );
        Similarity S = new Similarity(R, T, Matrix.identity(3,3).times(2));
        Matrix M = S.getHomogeneousMatrix();
        assertEquals(0, M.get(0, 0));
        assertEquals(2, M.get(1, 0));
        assertEquals(0, M.get(2, 0));
        assertEquals(0, M.get(3, 0));
        assertEquals(-2, M.get(0, 1));
        assertEquals(0, M.get(1, 1));
        assertEquals(0, M.get(2, 1));
        assertEquals(0, M.get(3, 1));
        assertEquals(0, M.get(0, 2));
        assertEquals(0, M.get(1, 2));
        assertEquals(2, M.get(2, 2));
        assertEquals(0, M.get(3, 2));
        assertEquals(1, M.get(0, 3));
        assertEquals(2, M.get(1, 3));
        assertEquals(3, M.get(2, 3));
        assertEquals(1, M.get(3, 3));
    }
}
