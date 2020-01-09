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
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;

public abstract class Rigid2DCovarianceMaxLikelihoodComputer  {

    protected MatrixUtil matrixUtil;

    public Rigid2DCovarianceMaxLikelihoodComputer(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    public Matrix compute(FiducialSet fiducialSet, Transformation transformation) {
        OptimizationResult optimizationResult = optimize(fiducialSet, transformation);
        double[] optimize = optimizationResult.getParameters();

        Matrix v = new Matrix(optimize,  fiducialSet.getTargetDataset().getDimension());
        Matrix lambdaInv = v.transpose().times(v);
        return matrixUtil.pseudoInverse(
            lambdaInv
        );
    }

    protected abstract OptimizationResult optimize(FiducialSet fiducialSet, Transformation transformation);
}
