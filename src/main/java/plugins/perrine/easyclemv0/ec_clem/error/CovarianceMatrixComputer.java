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
package plugins.perrine.easyclemv0.ec_clem.error;

import Jama.Matrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import plugins.perrine.easyclemv0.ec_clem.matrix.RealMatrixFactory;
import plugins.perrine.easyclemv0.ec_clem.matrix.MatrixFactory;

import javax.inject.Inject;

public class CovarianceMatrixComputer {

    private RealMatrixFactory realMatrixFactory;
    private MatrixFactory matrixFactory;

    @Inject
    public CovarianceMatrixComputer(RealMatrixFactory realMatrixFactory, MatrixFactory matrixFactory) {
        this.realMatrixFactory = realMatrixFactory;
        this.matrixFactory = matrixFactory;
    }

    public Matrix compute(Matrix M) {
        return matrixFactory.getFrom(
            new Covariance(realMatrixFactory.getFrom(M)).getCovarianceMatrix()
        );
    }
}
