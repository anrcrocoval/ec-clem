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
package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import Jama.QRDecomposition;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.transformation.Transformation;

import javax.inject.Inject;

public class TLSAffineTransformationComputer implements TransformationComputer {

    private MatrixUtil matrixUtil;

    @Inject
    public TLSAffineTransformationComputer(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    @Override
    public Transformation compute(FiducialSet fiducialSet) {
        Matrix A = fiducialSet.getSourceDataset().getMatrix();
        Matrix B = fiducialSet.getTargetDataset().getMatrix();

        A.svd().getS().print(1,5);

        Matrix C = new Matrix(fiducialSet.getN(), A.getColumnDimension() + B.getColumnDimension());
        C.setMatrix(0, C.getRowDimension() - 1, 0, A.getColumnDimension() - 1, A);
        C.setMatrix(0, C.getRowDimension() - 1, A.getColumnDimension(), C.getColumnDimension() - 1, B);

        System.out.println(C.rank());
        C.svd().getS().print(1,5);
        Matrix V11 = C.svd().getV().getMatrix(0, A.getColumnDimension() - 1, 0, A.getColumnDimension() - 1);
        Matrix V21 = C.svd().getV().getMatrix(A.getColumnDimension(), C.getColumnDimension() - 1, 0, A.getColumnDimension() - 1);
        Matrix X = V21.times(V11.inverse()).transpose();
        X.print(1,5);

        Matrix V = C.svd().getV();
        V.print(1,5);
        Matrix v = V.getMatrix(0, V.getRowDimension() - 1, V.getColumnDimension() - 1, V.getColumnDimension() - 1);
        QRDecomposition QR = v.qr();
        Matrix M = QR.getQ().transpose().times(v);
        M.print(1,5);
        return null;
    }
}
