package plugins.perrine.easyclemv0;

import Jama.Matrix;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.transformation.Transformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestFiducialSetFactory {

    private Random random = new Random();

    public FiducialSet getSimpleRotationFiducialSet() {
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
        return new FiducialSet(source, target);
    }

    public FiducialSet getSimpleTranslationFiducialSet() {
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
        return new FiducialSet(source, target);
    }

    public FiducialSet getComplexRotationFiducialSet() {
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
        return new FiducialSet(source, target);
    }

    public FiducialSet getTranslationRotationScalingFiducialSet() {
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
        return new FiducialSet(source, target);
    }

    public FiducialSet getRandomFromTransformation(Transformation transformation, int n) {
        Dataset source = new Dataset(3);
        for(int i = 0; i < n; i++) {
            source.addPoint(new Point(new double[]{ random.nextInt(255), random.nextInt(255), random.nextInt(255) }));
        }
        Dataset target = transformation.apply(source);
        return new FiducialSet(source, target);
    }
}
