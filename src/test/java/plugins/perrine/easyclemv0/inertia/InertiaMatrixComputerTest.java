package plugins.perrine.easyclemv0.inertia;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InertiaMatrixComputerTest {
    private InertiaMatrixComputer subjectUnderTest = new InertiaMatrixComputer();

    public InertiaMatrixComputerTest() {
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
