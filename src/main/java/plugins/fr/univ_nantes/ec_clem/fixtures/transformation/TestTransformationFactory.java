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
package plugins.fr.univ_nantes.ec_clem.fixtures.transformation;

import Jama.Matrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import plugins.fr.univ_nantes.ec_clem.transformation.AffineTransformation;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;
import javax.inject.Inject;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TestTransformationFactory {

    private SynchronizedRandomGenerator random = new SynchronizedRandomGenerator(new JDKRandomGenerator());

    @Inject
    public TestTransformationFactory() {}

    public Similarity getRandomSimpleRotationTransformation(int dimension) {
        return getSimpleRotationTransformation(dimension);
    }

    private double getRandomAngle() {
        return random.nextDouble() * 2 * Math.PI;
    }

    public Similarity getSimpleRotationTransformation(int dimension) {
        switch (dimension) {
            case 2: return getSimpleRotationTransformation2D(getRandomAngle());
            case 3: return getSimpleRotationTransformation3D(getRandomAngle(), getRandomAngle(), getRandomAngle());
        }
        return null;
    }

    public Similarity getRandomSimpleSimilarityTransformation2D() {
        return getSimpleSimilarityTransformation2D(getRandomAngle(), new double[]{random.nextDouble(), random.nextDouble()}, random.nextDouble());
    }

    public AffineTransformation getRandomSimpleAffineTransformation2D() {
        Matrix A = new Matrix(2, 2);
        A.set(0, 0, random.nextDouble());
        A.set(0, 1, random.nextDouble());
        A.set(1, 0, random.nextDouble());
        A.set(1, 1, random.nextDouble());
        Matrix T = new Matrix(2, 1);
        T.set(0, 0, random.nextDouble());
        T.set(1, 0, random.nextDouble());
        return new AffineTransformation(A, T);
    }

    public Similarity getSimpleSimilarityTransformation2D(double angle, double[] t, double scale) {
        Matrix R = new Matrix(
            new double[][]{
                { cos(angle), -1 * sin(angle) },
                { sin(angle), cos(angle) }
            }
        );

        Matrix S = Matrix.identity(2, 2).times(scale);
        Matrix T = new Matrix(
            new double[][] {
                { t[0] },
                { t[1] }
            }
        );

        return new Similarity(R, T, S);
    }

    public Similarity getSimpleRotationTransformation2D(double angle) {
        Matrix R = new Matrix(
            new double[][]{
                { cos(angle), -1 * sin(angle) },
                { sin(angle), cos(angle) }
            }
        );

        Matrix S = Matrix.identity(2, 2);
        Matrix T = new Matrix(
            new double[][] {
                { 0 },
                { 0 }
            }
        );

        return new Similarity(R, T, S);
    }

    public Similarity getSimpleRotationTransformation3D(double ox, double oy, double oz) {
        Matrix R = new Matrix(
            new double[][]{
                { cos(oz) * cos(oy), (cos(oz) * sin(oy) * sin(ox)) - (sin(oz) * cos(ox)), (cos(oz) * sin(oy) * cos(ox)) + (sin(oz) * sin(ox)) },
                { sin(oz) * cos(oy), (sin(oz) * sin(oy) * sin(ox)) + (cos(oz) * cos(ox)), (sin(oz) * sin(oy) * cos(ox)) - (cos(oz) * sin(ox)) },
                { -sin(oy), cos(oy) * sin(ox), cos(oy) * cos(ox) }
            }
        );

        Matrix S = Matrix.identity(3, 3);
        Matrix T = new Matrix(
            new double[][] {
                { 0 },
                { 0 },
                { 0 }
            }
        );

        return new Similarity(R, T, S);
    }
}
