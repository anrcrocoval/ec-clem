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
package fr.univ_nantes.ec_clem.fixtures.fiducialset;

import Jama.Matrix;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.transformation.Transformation;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFiducialSetFactory {

    private SynchronizedRandomGenerator random = new SynchronizedRandomGenerator(new JDKRandomGenerator());

    @Inject
    public TestFiducialSetFactory() {
    }

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

    public FiducialSet getRandomFromTransformation(Transformation transformation, int n, int[] range) {
        Dataset source = new Dataset(range.length);
        for(int i = 0; i < n; i++) {
            source.addPoint(getRandomPoint(range));
        }
        Dataset target = transformation.apply(source);
        return new FiducialSet(source, target);
    }

    private double[] getGaussian(double[][] covariance) {
        double[] means = new double[covariance.length];
        Arrays.fill(means, 0);
        MultivariateNormalDistribution multivariateNormalDistribution = new MultivariateNormalDistribution(random, means, covariance);
        return multivariateNormalDistribution.sample();
    }

    private double[] getUniform(int[] range) {
        double[] result = new double[range.length];
        for(int i = 0; i < range.length; i++) {
            result[i] = random.nextInt(range[i]);
        }
        return result;
    }

    public Point getRandomPoint(int[] range) {
        return new Point(getUniform(range));
    }

//    public FiducialSet getGaussianAroundCenterOfGravityFromTransformation(Transformation transformation, int n, int[] range, double[][] covariance) {
//        Point centerOfGravity = getRandomPoint(range);
//        Dataset source = new Dataset(3);
//        for(int i = 0; i < n; i++) {
//            source.addPoint(
//                centerOfGravity.plus(
//                    new Point(getGaussian(covariance))
//                )
//            );
//        }
//        Dataset target = transformation.apply(source);
//        return new FiducialSet(source, target);
//    }

    public FiducialSet getRandomAndNoisyFromTransformation(Transformation transformation, int n, int[] range, double[][] covariance) {
        FiducialSet fiducialSet = getRandomFromTransformation(transformation, n, range);
        addGaussianNoise(fiducialSet.getSourceDataset(), covariance);
        addGaussianNoise(fiducialSet.getTargetDataset(), covariance);
        return fiducialSet;
    }

    public Dataset addGaussianNoise(Dataset dataset, double[][] covariance) {
        for(int i = 0; i < dataset.getN(); i++) {
            Point point = dataset.getPoint(i);
            dataset.setPoint(i, addGaussianNoise(point, covariance));
        }
        return dataset;
    }

    public Point addGaussianNoise(Point point, double[][] covariance) {
        Point noise = new Point(getGaussian(covariance));
        return point.plus(noise);
    }
}
