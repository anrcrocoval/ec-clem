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
package plugins.perrine.easyclemv0.test.transformation;

import Jama.Matrix;
import plugins.perrine.easyclemv0.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TestTransformationFactory {

    @Inject
    public TestTransformationFactory() {
    }

    public Similarity getSimpleRotationTransformation(double angle) {
        Matrix R = new Matrix(
            new double[][]{
                { cos(angle), -1 * sin(angle), 0 },
                { sin(angle), cos(angle), 0 },
                { 0, 0, 1 }
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
