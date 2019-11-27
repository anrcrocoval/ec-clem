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
package plugins.fr.univ_nantes.ec_clem.registration;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.transformation.AffineTransformation;

import javax.inject.Inject;

public class AffineTransformationComputer implements TransformationComputer {

    private MatrixUtil matrixUtil;

    @Inject
    public AffineTransformationComputer(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    @Override
    public AffineTransformation compute(FiducialSet fiducialSet) {
        Matrix A = fiducialSet.getSourceDataset().getHomogeneousMatrixLeft();
        Matrix B = fiducialSet.getTargetDataset().getMatrix();
        Matrix result = matrixUtil.pseudoInverse(A.transpose().times(A)).times(A.transpose()).times(B).transpose();
        return new AffineTransformation(
            result.getMatrix(0, result.getRowDimension() - 1, 1, result.getColumnDimension() - 1),
            result.getMatrix(0, result.getRowDimension() - 1, 0, 0)
        );
    }
}
