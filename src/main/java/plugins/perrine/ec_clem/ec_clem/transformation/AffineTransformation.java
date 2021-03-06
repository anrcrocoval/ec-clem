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
package plugins.perrine.ec_clem.ec_clem.transformation;

import Jama.Matrix;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;

public class AffineTransformation implements Transformation {
    private Matrix A;
    protected Matrix T;

    public AffineTransformation(Matrix A, Matrix T) {
        this.A = A;
        this.T = T;
    }

    public AffineTransformation(Matrix M) {
        this.A = M.getMatrix(0, M.getRowDimension() - 2, 0, M.getColumnDimension() - 2);
        this.T = M.getMatrix(0, M.getRowDimension() - 2, M.getColumnDimension() - 1, M.getColumnDimension() - 1);
    }

    public Point apply(Point point) {
        return new Point((A.times(point.getMatrix())).plus(T));
    }

    // A then T
    public Dataset apply(Dataset dataset) {
        Matrix M = (getHomogeneousMatrix().times(dataset.getHomogeneousMatrixRight().transpose())).transpose();
        M = M.getMatrix(0, M.getRowDimension() - 1, 0, M.getColumnDimension() - 2);
        return new Dataset(M, dataset.getPointType());
    }

    public Matrix getMatrixRight() {
        Matrix M = new Matrix(A.getRowDimension(), A.getColumnDimension() + 1, 0);
        for(int i = 0; i < A.getRowDimension(); i++) {
            for(int j = 0; j < A.getColumnDimension(); j++) {
                M.set(i, j, A.get(i, j));
            }
        }
        for(int i = 0; i < T.getRowDimension(); i++) {
            M.set(i, M.getColumnDimension() - 1, T.get(i, 0));
        }
        return M;
    }

    public Matrix getMatrixLeft() {
        Matrix M = new Matrix(A.getRowDimension(), A.getColumnDimension() + 1, 0);
        for(int i = 0; i < A.getRowDimension(); i++) {
            for(int j = 0; j < A.getColumnDimension(); j++) {
                M.set(i, j + 1, A.get(i, j));
            }
        }
        for(int i = 0; i < T.getRowDimension(); i++) {
            M.set(i, 0, T.get(i, 0));
        }
        return M;
    }

    public Matrix getHomogeneousMatrix() {
        Matrix H = getMatrixRight();
        Matrix M = new Matrix(H.getRowDimension() + 1, H.getColumnDimension(), 0);
        M.setMatrix(0, H.getRowDimension() - 1, 0, H.getColumnDimension() - 1, H);
        M.set(M.getRowDimension() - 1, M.getColumnDimension() - 1, 1);
       
        return M;
    }
    public Matrix getHomogeneousMatrix4x4() {
        Matrix H = getMatrixRight();
        Matrix M = new Matrix(H.getRowDimension() + 1, H.getColumnDimension(), 0);
        M.setMatrix(0, H.getRowDimension() - 1, 0, H.getColumnDimension() - 1, H);
        M.set(M.getRowDimension() - 1, M.getColumnDimension() - 1, 1);
        if (M.getRowDimension()==3)//2d only..
        {
        	Matrix correctedM=new Matrix(4,4,0);
        	correctedM.setMatrix(0, 1, 0,1,M.getMatrix(0, 1, 0, 1));
        	correctedM.set(2, 2, 1.0);
        	correctedM.setMatrix(0,1,3,3,M.getMatrix(0, 1,2,2));
        	correctedM.set(3, 3, 1.0);
        	M=correctedM;
        	
        }
        return M;
    }
}
