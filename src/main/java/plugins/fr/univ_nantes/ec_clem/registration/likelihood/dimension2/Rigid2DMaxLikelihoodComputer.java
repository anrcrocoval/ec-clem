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
package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;
import plugins.fr.univ_nantes.ec_clem.registration.TransformationComputer;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public abstract class Rigid2DMaxLikelihoodComputer implements TransformationComputer {

    protected MatrixUtil matrixUtil;

    public Rigid2DMaxLikelihoodComputer() {
        DaggerRigid2DMaxLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    public Similarity compute(FiducialSet fiducialSet) {
        double[] optimize = optimize(fiducialSet);
        Similarity s = new Similarity(
            new Matrix(new double [][] {
                { cos(optimize[2]), -sin(optimize[2]) },
                { sin(optimize[2]), cos(optimize[2]) }
            }),
            new Matrix(new double[][] {
                {  optimize[0] },
                { optimize[1] }
            }),
            Matrix.identity(2,2)
        );

        double lambdaInv11 = (Math.pow(optimize[3], 2) + Math.pow(optimize[5], 2));
        double lambdaInv22 = (Math.pow(optimize[6], 2) + Math.pow(optimize[4], 2));
        double lambdaInv12 = (optimize[3] * optimize[4]) + (optimize[6] * optimize[5]);
        double det = (lambdaInv11 * lambdaInv22) - (lambdaInv12 * lambdaInv12);
//        System.out.println(lambdaInv22 / det);
//        System.out.println(-lambdaInv12 / det);
//        System.out.println(lambdaInv11 / det);
        return s;
    }

    protected abstract double[] optimize(FiducialSet fiducialSet);

    @Inject
    public void setMatrixUtil(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }
}
