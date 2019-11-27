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
package fr.univ_nantes.ec_clem.test.fiducialset.dataset;

import Jama.Matrix;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

class DatasetTest {
    private Dataset subjectUnderTest;

    @BeforeTest
    void init() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        points.add(new Point(new Matrix(new double[][] {{ 4 }, { 5 }, { 6 }})));
        points.add(new Point(new Matrix(new double[][] {{ 7 }, { 8 }, { 9 }})));
        subjectUnderTest = new Dataset(points);
    }

    @Test
    void getBarycentreTest() {
        Point barycentre = subjectUnderTest.getBarycentre();
        assertEquals(4, barycentre.getMatrix().get(0, 0));
        assertEquals(5, barycentre.getMatrix().get(1, 0));
        assertEquals(6, barycentre.getMatrix().get(2, 0));
        assertEquals(3, barycentre.getDimension());
    }

    @Test
    void substractBarycentre() {
        subjectUnderTest.substractBarycentre();
        assertEquals(-3,subjectUnderTest.getMatrix().get(0, 0));
        assertEquals(0, subjectUnderTest.getMatrix().get(1, 0));
        assertEquals(3, subjectUnderTest.getMatrix().get(2, 0));
        assertEquals(-3, subjectUnderTest.getMatrix().get(0, 1));
        assertEquals(0, subjectUnderTest.getMatrix().get(1, 1));
        assertEquals(3, subjectUnderTest.getMatrix().get(2, 1));
        assertEquals(-3, subjectUnderTest.getMatrix().get(0, 2));
        assertEquals(0, subjectUnderTest.getMatrix().get(1, 2));
        assertEquals(3, subjectUnderTest.getMatrix().get(2, 2));
        assertEquals(3, subjectUnderTest.getN());
        assertEquals(3, subjectUnderTest.getDimension());
    }

    @Test
    void getMeanNormTest() {
        double meanNorm = subjectUnderTest.getMeanNorm();
        assertEquals(8.815, meanNorm, 0.0001);
    }

    @Test
    void testClone() {
        Dataset clone = subjectUnderTest.clone();
        assertNotSame(clone, subjectUnderTest);
        assertSame(clone.getClass(), subjectUnderTest.getClass());
    }
}
