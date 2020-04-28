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
package plugins.fr.univ_nantes.ec_clem.ec_clem.registration;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.ec_clem.matrix.MatrixUtil;
import javax.inject.Inject;

public class RigidRegistrationParameterComputer extends SimilarityRegistrationParameterComputer {

    @Inject
    public RigidRegistrationParameterComputer(MatrixUtil matrixUtil) {
        super(matrixUtil);
    }

    @Override
    protected Matrix getS(Matrix sourceDataset, Matrix targetDataset, Matrix R) {
        return Matrix.identity(R.getRowDimension(), R.getColumnDimension());
    }
}
