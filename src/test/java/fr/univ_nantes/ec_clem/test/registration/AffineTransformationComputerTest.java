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
package fr.univ_nantes.ec_clem.test.registration;

import Jama.Matrix;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.registration.AffineTransformationComputer;
import plugins.fr.univ_nantes.ec_clem.transformation.AffineTransformation;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

class AffineTransformationComputerTest {
    private AffineTransformationComputer subjectUnderTest;

    public AffineTransformationComputerTest() {
        DaggerAffineTransformationComputerTestComponent.create().inject(this);
    }

    @Inject
    public void setSubjectUnderTest(AffineTransformationComputer subjectUnderTest) {
        this.subjectUnderTest = subjectUnderTest;
    }

    @Test
    void simpleRotation() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ -4 }, { 2 }, { 2 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -4 }, { -2 }, { 4 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{  4 }, { 0 }, { 6 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -4 }, { 2 }, { 6 }})));
        Dataset target = new Dataset(targetPoints);

        AffineTransformation result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(4, result.getHomogeneousMatrix().getRowDimension());
        assertEquals(4, result.getHomogeneousMatrix().getColumnDimension());
        assertEquals(0, result.getHomogeneousMatrix().get(0, 0), 0.0000000001);
        assertEquals(2, result.getHomogeneousMatrix().get(1, 0), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(2, 0), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 0), 0.0000000001);
        assertEquals(-2, result.getHomogeneousMatrix().get(0, 1), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 1), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(2, 1), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 1), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(0, 2), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 2), 0.0000000001);
        assertEquals(2, result.getHomogeneousMatrix().get(2, 2), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 2), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(0, 3), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 3), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(2, 3), 0.0000000001);
        assertEquals(1, result.getHomogeneousMatrix().get(3, 3), 0.0000000001);
    }

    @Test
    void simpleTranslation() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ 2 }, { 3 }, { 2 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 0 }, { 3 }, { 3 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 1 }, { -1 }, { 4 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 2 }, { 3 }, { 4 }})));
        Dataset target = new Dataset(targetPoints);

        AffineTransformation result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(4, result.getHomogeneousMatrix().getRowDimension());
        assertEquals(4, result.getHomogeneousMatrix().getColumnDimension());
        assertEquals(1, result.getHomogeneousMatrix().get(0, 0), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 0), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(2, 0), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 0), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(0, 1), 0.0000000001);
        assertEquals(1, result.getHomogeneousMatrix().get(1, 1), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(2, 1), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 1), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(0, 2), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 2), 0.0000000001);
        assertEquals(1, result.getHomogeneousMatrix().get(2, 2), 0.0000000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 2), 0.0000000001);
        assertEquals(1, result.getHomogeneousMatrix().get(0, 3), 0.0000001);
        assertEquals(1, result.getHomogeneousMatrix().get(1, 3), 0.0000000001);
        assertEquals(1, result.getHomogeneousMatrix().get(2, 3), 0.0000000001);
        assertEquals(1, result.getHomogeneousMatrix().get(3, 3), 0.0000000001);
    }

    @Test
    void complexRotation() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ -2.1213204 }, { 1 }, { -0.7071068 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -2.8284272 }, { -1 }, { 0 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -0.7071068 }, { 0 }, { 3.5355340 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -3.5355340 }, { 1 }, { 0.7071068 }})));
        Dataset target = new Dataset(targetPoints);

        AffineTransformation result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(4, result.getHomogeneousMatrix().getRowDimension());
        assertEquals(4, result.getHomogeneousMatrix().getColumnDimension());
        assertEquals(0, result.getHomogeneousMatrix().get(0, 0), 0.0000001);
        assertEquals(1, result.getHomogeneousMatrix().get(1, 0), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(2, 0), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 0), 0.0000001);
        assertEquals(-0.7071068, result.getHomogeneousMatrix().get(0, 1), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 1), 0.0000001);
        assertEquals(-0.7071068, result.getHomogeneousMatrix().get(2, 1), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 1), 0.0000001);
        assertEquals(-0.7071068, result.getHomogeneousMatrix().get(0, 2), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 2), 0.0000001);
        assertEquals(0.7071068, result.getHomogeneousMatrix().get(2, 2), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(3, 2), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(0, 3), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(1, 3), 0.0000001);
        assertEquals(0, result.getHomogeneousMatrix().get(2, 3), 0.000001);
        assertEquals(1, result.getHomogeneousMatrix().get(3, 3), 0.000001);
    }
}
