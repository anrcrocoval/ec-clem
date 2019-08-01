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
package plugins.perrine.easyclemv0.test.error;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.error.fitzpatrick.InertiaMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InertiaMatrixComputerTest {
    private InertiaMatrixComputer subjectUnderTest;

    public InertiaMatrixComputerTest() {
        DaggerInertiaMatrixComputerTestComponent.create().inject(this);
    }

    @Inject
    public void setSubjectUnderTest(InertiaMatrixComputer subjectUnderTest) {
        this.subjectUnderTest = subjectUnderTest;
    }

    @Test
    void getInertiaMatrix3D() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        points.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        points.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        points.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset dataset = new Dataset(points);
        Matrix inertiaMatrix = subjectUnderTest.getInertiaMatrix(dataset);
        assertEquals(dataset.getDimension(), inertiaMatrix.getColumnDimension());
        assertEquals(dataset.getDimension(), inertiaMatrix.getRowDimension());
        assertEquals(39, inertiaMatrix.get(0, 0), 0.00001);
        assertEquals(-2, inertiaMatrix.get(1, 0), 0.00001);
        assertEquals(-2, inertiaMatrix.get(2, 0), 0.00001);
        assertEquals(-2, inertiaMatrix.get(0, 1), 0.00001);
        assertEquals(26, inertiaMatrix.get(1, 1), 0.00001);
        assertEquals(-6, inertiaMatrix.get(2, 1), 0.00001);
        assertEquals(-2, inertiaMatrix.get(0, 2), 0.00001);
        assertEquals(-6, inertiaMatrix.get(1, 2), 0.00001);
        assertEquals(19, inertiaMatrix.get(2, 2), 0.00001);
    }

    @Test
    void getInertiaMatrix2D() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }})));
        points.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }})));
        points.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }})));
        points.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }})));
        Dataset dataset = new Dataset(points);
        Matrix inertiaMatrix = subjectUnderTest.getInertiaMatrix(dataset);
        assertEquals(dataset.getDimension(), inertiaMatrix.getColumnDimension());
        assertEquals(dataset.getDimension(), inertiaMatrix.getRowDimension());
        assertEquals(16, inertiaMatrix.get(0, 0), 0.00001);
        assertEquals(-2, inertiaMatrix.get(1, 0), 0.00001);
        assertEquals(-2, inertiaMatrix.get(0, 1), 0.00001);
        assertEquals(3, inertiaMatrix.get(1, 1), 0.00001);
    }
}
