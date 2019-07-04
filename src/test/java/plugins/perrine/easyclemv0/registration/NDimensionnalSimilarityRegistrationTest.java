package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.transformation.Similarity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NDimensionnalSimilarityRegistrationTest {
    private NDimensionnalSimilarityRegistration subjectUnderTest = new NDimensionnalSimilarityRegistration();

    @Test
    void simpleRotation() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ -1 }, { 2 }, { 2 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 0 }, { -2 }, { 3 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ -2 }, { 1 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -2 }, { -1 }, { 2 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{  2 }, { 0 }, { 3 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ -2 }, { 1 }, { 3 }})));
        Dataset target = new Dataset(targetPoints);

        Similarity result = subjectUnderTest.apply(source, target);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        assertEquals(0, result.getR().get(0, 0), 0.0000000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000000001);
        assertEquals(-1, result.getR().get(0, 1), 0.0000000001);
        assertEquals(0, result.getR().get(1, 1), 0.0000000001);
        assertEquals(0, result.getR().get(2, 1), 0.0000000001);
        assertEquals(0, result.getR().get(0, 2), 0.0000000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000000001);
        assertEquals(1, result.getR().get(2, 2), 0.0000000001);
        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.0000000001);
        assertEquals(0, result.getT().get(1, 0), 0.0000000001);
        assertEquals(0, result.getT().get(2, 0), 0.0000000001);
        assertEquals(1, result.getScale(), 0.0000000001);
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

        Similarity result = subjectUnderTest.apply(source, target);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        assertEquals(1, result.getR().get(0, 0), 0.0000000001);
        assertEquals(0, result.getR().get(1, 0), 0.0000000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000000001);
        assertEquals(0, result.getR().get(0, 1), 0.0000000001);
        assertEquals(1, result.getR().get(1, 1), 0.0000000001);
        assertEquals(0, result.getR().get(2, 1), 0.0000000001);
        assertEquals(0, result.getR().get(0, 2), 0.0000000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000000001);
        assertEquals(1, result.getR().get(2, 2), 0.0000000001);
        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(1, result.getT().get(0, 0), 0.0000001);
        assertEquals(1, result.getT().get(1, 0), 0.0000000001);
        assertEquals(1, result.getT().get(2, 0), 0.0000000001);
        assertEquals(1, result.getScale(), 0.0000000001);
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

        Similarity result = subjectUnderTest.apply(source, target);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        assertEquals(0, result.getR().get(0, 0), 0.0000001);
        assertEquals(1, result.getR().get(1, 0), 0.0000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(0, 1), 0.0000001);
        assertEquals(0, result.getR().get(1, 1), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(2, 1), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(0, 2), 0.0000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000001);
        assertEquals(0.7071068, result.getR().get(2, 2), 0.0000001);
        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.0000001);
        assertEquals(0, result.getT().get(1, 0), 0.0000001);
        assertEquals(0, result.getT().get(2, 0), 0.000001);
        assertEquals(1, result.getScale(), 0.0000001);
    }
}
