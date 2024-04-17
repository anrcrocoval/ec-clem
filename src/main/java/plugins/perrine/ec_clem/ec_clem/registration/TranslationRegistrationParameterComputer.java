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
package plugins.perrine.ec_clem.ec_clem.registration;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import plugins.perrine.ec_clem.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.matrix.MatrixUtil;
import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.transformation.Similarity;
import javax.inject.Inject;

public class TranslationRegistrationParameterComputer extends AffineRegistrationParameterComputer {

    @Inject
    public TranslationRegistrationParameterComputer(MatrixUtil matrixUtil) {
        super(matrixUtil);
    } 

    public RegistrationParameter compute(FiducialSet fiducialSet) {
        Dataset clonedSourceDataset = fiducialSet.getSourceDataset().clone();
        Dataset clonedTargetDataset = fiducialSet.getTargetDataset().clone();
        Point sourceBarycentre = clonedSourceDataset.getBarycentre();
        Point targetBarycentre = clonedTargetDataset.getBarycentre();
        
        clonedSourceDataset.substractBarycentre();
        clonedTargetDataset.substractBarycentre();
        
        Matrix R = Matrix.identity(fiducialSet.getTargetDataset().getDimension(), fiducialSet.getTargetDataset().getDimension()+1);
        
        Matrix S = Matrix.identity(fiducialSet.getTargetDataset().getDimension(), fiducialSet.getTargetDataset().getDimension()+1);
        Matrix T = getT(sourceBarycentre.getMatrix(), targetBarycentre.getMatrix(), R, S);
        
        Similarity similarity = new Similarity(R, T, S);
        Matrix residuals = fiducialSet.getTargetDataset().getMatrix().minus(
            similarity.apply(fiducialSet.getSourceDataset()).getMatrix()
        );

        double sum = 0;
        for(int i = 0; i < residuals.getRowDimension(); i++) {
            Matrix current = residuals.getMatrix(i, i, 0, residuals.getColumnDimension() - 1);
            Matrix times = (current).times(current.transpose());
            assert times.getRowDimension() == 1;
            assert times.getColumnDimension() == 1;
            sum += times.get(0, 0);
        }
        Matrix lambda = Matrix.identity(fiducialSet.getTargetDataset().getDimension(), fiducialSet.getTargetDataset().getDimension())
            .times(sum / (double) (fiducialSet.getN() * fiducialSet.getTargetDataset().getDimension()));

        return new RegistrationParameter(
            similarity,
            lambda,
            getLogLikelihood(residuals, lambda)
        );
    }

    protected Matrix getS(Matrix sourceDataset, Matrix targetDataset, Matrix R) {
        Dataset ratioDataset = new Dataset(targetDataset.arrayRightDivide(R.times(sourceDataset.transpose()).transpose()), PointType.FIDUCIAL);
        Point ratioPoint = ratioDataset.getBarycentre();
        Matrix scale = Matrix.identity(R.getRowDimension(), R.getColumnDimension());
        for(int i = 0; i < R.getRowDimension(); i++) {
            if(Double.isNaN(ratioPoint.get(i)) || ratioPoint.get(i) == 0) {
                scale.set(i, i, 1);
            } else {
                scale.set(i, i, ratioPoint.get(i));
            }
        }
        return scale;
    }

    

    private Matrix getT(Matrix sourceBarycentre, Matrix targetBarycentre, Matrix R, Matrix scale) {
        return targetBarycentre.minus(R.times(scale).times(sourceBarycentre));
    }
}
