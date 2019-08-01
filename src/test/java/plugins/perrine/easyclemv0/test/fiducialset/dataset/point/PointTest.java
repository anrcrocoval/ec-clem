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
package plugins.perrine.easyclemv0.test.fiducialset.dataset.point;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointTest {
    private Point subjectUnderTest;

    @Test
    void getDistance2DTest() {
        Matrix axis = new Matrix(new double[][] {
            { 3 }, { 0 }
        });
        Matrix pointOnTheAxis = new Matrix(2, 1, 0);
        subjectUnderTest = new Point(new Matrix(new double[][] {
            { 2 }, { 7 }
        }));
        assertEquals(Math.pow(7, 2), subjectUnderTest.getSquareDistance(axis, pointOnTheAxis));
    }

    @Test
    void getDistance3DTest() {
        Matrix axis = new Matrix(new double[][] {
            { 3 }, { 0 }, { 0 }
        });
        Matrix pointOnTheAxis = new Matrix(3, 1, 0);
        subjectUnderTest = new Point(new Matrix(new double[][] {
            { 1 }, { 1 }, { 1 }
        }));
        assertEquals(2, subjectUnderTest.getSquareDistance(axis, pointOnTheAxis));
    }
}
