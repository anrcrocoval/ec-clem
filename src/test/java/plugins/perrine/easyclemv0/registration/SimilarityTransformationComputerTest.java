package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.transformation.Similarity;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimilarityTransformationComputerTest {
    private SimilarityTransformationComputer subjectUnderTest = new SimilarityTransformationComputer();

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

        Similarity result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(source, target, result);
        assertEquals(0, result.getR().get(0, 0), 0.0000000001);
        assertEquals(1, result.getR().get(1, 0), 0.0000000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000000001);
        assertEquals(-1, result.getR().get(0, 1), 0.0000000001);
        assertEquals(0, result.getR().get(1, 1), 0.0000000001);
        assertEquals(0, result.getR().get(2, 1), 0.0000000001);
        assertEquals(0, result.getR().get(0, 2), 0.0000000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000000001);
        assertEquals(1, result.getR().get(2, 2), 0.0000000001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(2, result.getS().get(0, 0), 0.0000000001);
        assertEquals(0, result.getS().get(1, 0), 0.0000000001);
        assertEquals(0, result.getS().get(2, 0), 0.0000000001);
        assertEquals(0, result.getS().get(0, 1), 0.0000000001);
        assertEquals(2, result.getS().get(1, 1), 0.0000000001);
        assertEquals(0, result.getS().get(2, 1), 0.0000000001);
        assertEquals(0, result.getS().get(0, 2), 0.0000000001);
        assertEquals(0, result.getS().get(1, 2), 0.0000000001);
        assertEquals(2, result.getS().get(2, 2), 0.0000000001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.0000000001);
        assertEquals(0, result.getT().get(1, 0), 0.0000000001);
        assertEquals(0, result.getT().get(2, 0), 0.0000000001);
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

        Similarity result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(source, target, result);
        assertEquals(1, result.getR().get(0, 0), 0.0000000001);
        assertEquals(0, result.getR().get(1, 0), 0.0000000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000000001);
        assertEquals(0, result.getR().get(0, 1), 0.0000000001);
        assertEquals(1, result.getR().get(1, 1), 0.0000000001);
        assertEquals(0, result.getR().get(2, 1), 0.0000000001);
        assertEquals(0, result.getR().get(0, 2), 0.0000000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000000001);
        assertEquals(1, result.getR().get(2, 2), 0.0000000001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.0000000001);
        assertEquals(0, result.getS().get(1, 0), 0.0000000001);
        assertEquals(0, result.getS().get(2, 0), 0.0000000001);
        assertEquals(0, result.getS().get(0, 1), 0.0000000001);
        assertEquals(1, result.getS().get(1, 1), 0.0000000001);
        assertEquals(0, result.getS().get(2, 1), 0.0000000001);
        assertEquals(0, result.getS().get(0, 2), 0.0000000001);
        assertEquals(0, result.getS().get(1, 2), 0.0000000001);
        assertEquals(1, result.getS().get(2, 2), 0.0000000001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(1, result.getT().get(0, 0), 0.0000001);
        assertEquals(1, result.getT().get(1, 0), 0.0000000001);
        assertEquals(1, result.getT().get(2, 0), 0.0000000001);
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

        Similarity result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(source, target, result);
        assertEquals(0, result.getR().get(0, 0), 0.0000001);
        assertEquals(1, result.getR().get(1, 0), 0.0000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(0, 1), 0.0000001);
        assertEquals(0, result.getR().get(1, 1), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(2, 1), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(0, 2), 0.0000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000001);
        assertEquals(0.7071068, result.getR().get(2, 2), 0.0000001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.0000001);
        assertEquals(0, result.getS().get(1, 0), 0.0000001);
        assertEquals(0, result.getS().get(2, 0), 0.0000001);
        assertEquals(0, result.getS().get(0, 1), 0.0000001);
        assertEquals(1, result.getS().get(1, 1), 0.0000001);
        assertEquals(0, result.getS().get(2, 1), 0.0000001);
        assertEquals(0, result.getS().get(0, 2), 0.0000001);
        assertEquals(0, result.getS().get(1, 2), 0.0000001);
        assertEquals(1, result.getS().get(2, 2), 0.0000001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.0000001);
        assertEquals(0, result.getT().get(1, 0), 0.0000001);
        assertEquals(0, result.getT().get(2, 0), 0.000001);
    }

    @Test
    void translationRotationScaling() {
        List<Point> sourcePoints = new ArrayList<>();
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 168.04540 }, { 158.13620 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 343.47724 }, { 298.73880 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 236.15495 }, { 427.03367 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 59.66200 }, { 306.42565 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 123.16499 }, { 226.27039 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 189.46474 }, { 271.82878 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 229.31887 }, { 321.75632 }, { 1 }})));
        sourcePoints.add(new Point(new Matrix(new double[][] {{ 298.71181 }, { 368.81018 }, { 1 }})));
        Dataset source = new Dataset(sourcePoints);

        List<Point> targetPoints = new ArrayList<>();
        targetPoints.add(new Point(new Matrix(new double[][] {{ 160 }, { 306 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 610 }, { 332 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 576 }, { 674 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 144 }, { 670 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 160 }, { 470 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 326 }, { 474 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 442 }, { 498 }, { 1 }})));
        targetPoints.add(new Point(new Matrix(new double[][] {{ 608 }, { 502 }, { 1 }})));
        Dataset target = new Dataset(targetPoints);

        Similarity result = subjectUnderTest.compute(new FiducialSet(source, target));
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(source, target, result);
        assertEquals(0.82310, result.getR().get(0, 0), 0.00001);
        assertEquals(-0.56789, result.getR().get(1, 0), 0.00001);
        assertEquals(0, result.getR().get(2, 0), 0.00001);
        assertEquals(0.56789, result.getR().get(0, 1), 0.00001);
        assertEquals(0.82310, result.getR().get(1, 1), 0.00001);
        assertEquals(0, result.getR().get(2, 1), 0.00001);
        assertEquals(0, result.getR().get(0, 2), 0.00001);
        assertEquals(0, result.getR().get(1, 2), 0.00001);
        assertEquals(1, result.getR().get(2, 2), 0.00001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(1.97705, result.getS().get(0, 0), 0.00001);
        assertEquals(0, result.getS().get(1, 0), 0.00001);
        assertEquals(0, result.getS().get(2, 0), 0.00001);
        assertEquals(0, result.getS().get(0, 1), 0.00001);
        assertEquals(1.77642, result.getS().get(1, 1), 0.00001);
        assertEquals(0, result.getS().get(2, 1), 0.00001);
        assertEquals(0, result.getS().get(0, 2), 0.00001);
        assertEquals(0, result.getS().get(1, 2), 0.00001);
        assertEquals(1, result.getS().get(2, 2), 0.00001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(-256.97393, result.getT().get(0, 0), 0.0001);
        assertEquals(287.22428, result.getT().get(1, 0), 0.0001);
        assertEquals(0, result.getT().get(2, 0), 0.00001);
    }

    private void checkDetIsOne(Similarity result) {
        assertEquals(1, result.getR().det(), 0.0000000001);
    }

    private void checkExpectedResidualIsZero(Dataset sourceDataset, Dataset targetDataset, Similarity result) {
        Point expected = new Dataset(
                targetDataset.getMatrix().minus(
                        sourceDataset.getHomogeneousMatrixRight().times(result.getMatrixRight().transpose())
                )
        ).getBarycentre();
        for(int i = 0; i < expected.getDimension(); i++) {
            assertEquals(0, expected.get(i), 0.0000001);
        }
    }
}
