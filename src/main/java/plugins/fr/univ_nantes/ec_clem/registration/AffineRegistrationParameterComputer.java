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
import static java.lang.Math.*;

public class AffineRegistrationParameterComputer implements RegistrationParameterComputer {

    private MatrixUtil matrixUtil;

    @Inject
    public AffineRegistrationParameterComputer(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    @Override
    public RegistrationParameter compute(FiducialSet fiducialSet) {
        Matrix A = fiducialSet.getSourceDataset().getHomogeneousMatrixLeft();
        Matrix B = fiducialSet.getTargetDataset().getMatrix();
        Matrix result = (matrixUtil.pseudoInverse(A.transpose().times(A)).times(A.transpose()).times(B));

        AffineTransformation affineTransformation = new AffineTransformation(
            result.getMatrix(1, result.getRowDimension() - 1, 0, result.getColumnDimension() - 1).transpose(),
            result.getMatrix(0, 0, 0, result.getColumnDimension() - 1).transpose()
        );

        Matrix residuals = fiducialSet.getTargetDataset().getMatrix().minus(
            affineTransformation.apply(fiducialSet.getSourceDataset()).getMatrix()
        );

        Matrix covariance = residuals.transpose().times(residuals)
            .times((double) 1 / (fiducialSet.getN()));

        return new RegistrationParameter(
            affineTransformation,
            covariance,
            getLogLikelihood(residuals, covariance)
        );
    }

    protected double getLogLikelihood(Matrix residuals, Matrix covariance) {
        Matrix inverseCovariance = matrixUtil.pseudoInverse(covariance);
        double sum = 0;
        for(int i = 0; i < residuals.getRowDimension(); i++) {
            Matrix current = residuals.getMatrix(i, i, 0, residuals.getColumnDimension() - 1);
            sum += (current).times(inverseCovariance).times(current.transpose()).get(0, 0);
        }
        return (log(sqrt(inverseCovariance.det()) / (2d * PI)) * residuals.getRowDimension() - sum / 2d);
    }
}
