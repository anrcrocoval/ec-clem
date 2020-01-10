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
import Jama.SingularValueDecomposition;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DCovarianceMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;

import javax.inject.Inject;
import javax.inject.Named;

import static java.lang.Math.max;

public class SimilarityRegistrationParameterComputer extends AffineRegistrationParameterComputer {



    @Inject
    public SimilarityRegistrationParameterComputer(MatrixUtil matrixUtil, Rigid2DCovarianceMaxLikelihoodComputer rigid2DCovarianceMaxLikelihoodComputer) {
        super(matrixUtil, rigid2DCovarianceMaxLikelihoodComputer);

    }

    public RegistrationParameter compute(FiducialSet fiducialSet) {
        if (fiducialSet.getN() < 2) {
            int dimension = max(fiducialSet.getSourceDataset().getDimension(), fiducialSet.getTargetDataset().getDimension());
            return new RegistrationParameter(
                new Similarity(
                    Matrix.identity(
                        dimension,
                        dimension
                    ),
                    new Matrix(dimension, 1, 0),
                    Matrix.identity(dimension, dimension)
                ),
                Matrix.identity(dimension, dimension),
                Double.NaN
            );
        }

        Dataset clonedSourceDataset = fiducialSet.getSourceDataset().clone();
        Dataset clonedTargetDataset = fiducialSet.getTargetDataset().clone();
        Point sourceBarycentre = clonedSourceDataset.getBarycentre();
        Point targetBarycentre = clonedTargetDataset.getBarycentre();

        clonedSourceDataset.substractBarycentre();
        clonedTargetDataset.substractBarycentre();

        Matrix R = getR(clonedSourceDataset, clonedTargetDataset);
        Matrix S = getS(clonedSourceDataset.getMatrix(), clonedTargetDataset.getMatrix(), R);
        Matrix T = getT(sourceBarycentre.getMatrix(), targetBarycentre.getMatrix(), R, S);
        Similarity similarity = new Similarity(R, T, S);
        Matrix residuals = clonedTargetDataset.getMatrix().minus(
            similarity.apply(clonedSourceDataset).getMatrix()
        );


//        Matrix covariance = residuals.transpose().times(residuals).times((double) 1 / (fiducialSet.getN()));
        RegistrationParameter registrationParameter = rigid2DCovarianceMaxLikelihoodComputer.compute(fiducialSet, similarity);
//        similarity.getHomogeneousMatrix().print(1,5);
//        registrationParameter.getNoiseCovariance().print(1,5);
//        System.out.println(registrationParameter.getLogLikelihood());
//
//        System.out.println("Isotropic rigid (schonnemann)");
//        return new RegistrationParameter(
//            similarity,
//            covariance,
//            getLogLikelihood(residuals, covariance)
//        );
        return new RegistrationParameter(
            similarity,
            registrationParameter.getNoiseCovariance(),
            registrationParameter.getLogLikelihood()
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

    private Matrix getR(Dataset source, Dataset target) {
        Matrix S = target.getMatrix().transpose().times(source.getMatrix());
        SingularValueDecomposition svd = S.svd();
        Matrix E = Matrix.identity(svd.getS().getRowDimension(), svd.getS().getColumnDimension());
        E.set(E.getRowDimension() - 1, E.getColumnDimension() - 1, Math.signum(svd.getU().times(svd.getV().transpose()).det()));
        return svd.getU().times(E).times(svd.getV().transpose());
    }

    private Matrix getT(Matrix sourceBarycentre, Matrix targetBarycentre, Matrix R, Matrix scale) {
        return targetBarycentre.minus(R.times(scale).times(sourceBarycentre));
    }

//    @Inject
//    public void setRigid2DCovarianceMaxLikelihoodComputer(Rigid2DCovarianceMaxLikelihoodComputer rigid2DCovarianceMaxLikelihoodComputer) {
//        this.rigid2DCovarianceMaxLikelihoodComputer = rigid2DCovarianceMaxLikelihoodComputer;
//    }
}
