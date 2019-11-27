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
package fr.univ_nantes.ec_clem.test;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.error.CovarianceMatrixComputer;
import plugins.fr.univ_nantes.ec_clem.error.ErrorComputer;
import plugins.fr.univ_nantes.ec_clem.error.ExtendedKalmanFilter;
import plugins.fr.univ_nantes.ec_clem.error.KalmanFilterState;
import plugins.fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.fixtures.transformation.TestTransformationFactory;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.registration.AffineTransformationComputer;
import plugins.fr.univ_nantes.ec_clem.registration.TLSAffineTransformationComputer;
import plugins.fr.univ_nantes.ec_clem.transformation.AffineTransformation;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;
import plugins.fr.univ_nantes.ec_clem.registration.RigidTransformationComputer;
import javax.inject.Inject;

class IgnoredTest {

    private TestTransformationFactory testTransformationFactory;
    private TestFiducialSetFactory testFiducialSetFactory;
    private RigidTransformationComputer rigidTransformationComputer;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private ErrorComputer errorComputer;
    private ExtendedKalmanFilter extendedKalmanFilter;
    private TLSAffineTransformationComputer tlsAffineTransformationComputer;
    private AffineTransformationComputer affineTransformationComputer;

    private int[] range = new int[] {256, 256, 256};
    private double[][] isotropicCovariance = new double[][] {
        {100, 0, 0},
        {0, 100, 0},
        {0, 0, 100}
    };

    public IgnoredTest() {
        DaggerIgnoredTestComponent.create().inject(this);
    }

    @Inject
    public void setTestTransformationFactory(TestTransformationFactory testTransformationFactory) {
        this.testTransformationFactory = testTransformationFactory;
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }

    @Inject
    public void setRigidTransformationComputer(RigidTransformationComputer rigidTransformationComputer) {
        this.rigidTransformationComputer = rigidTransformationComputer;
    }

    @Inject
    public void setCovarianceMatrixComputer(CovarianceMatrixComputer covarianceMatrixComputer) {
        this.covarianceMatrixComputer = covarianceMatrixComputer;
    }

    @Inject
    public void setErrorComputer(ErrorComputer errorComputer) {
        this.errorComputer = errorComputer;
    }

    @Inject
    public void setExtendedKalmanFilter(ExtendedKalmanFilter extendedKalmanFilter) {
        this.extendedKalmanFilter = extendedKalmanFilter;
    }

    @Inject
    public void setTlsAffineTransformationComputer(TLSAffineTransformationComputer tlsAffineTransformationComputer) {
        this.tlsAffineTransformationComputer = tlsAffineTransformationComputer;
    }

    @Inject
    public void setAffineTransformationComputer(AffineTransformationComputer affineTransformationComputer) {
        this.affineTransformationComputer = affineTransformationComputer;
    }

    @Test(enabled = false)
    void LS() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(range.length);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n, range, isotropicCovariance);
        AffineTransformation compute = affineTransformationComputer.compute(randomFromTransformationFiducialSet);
        Dataset error = new Dataset(randomFromTransformationFiducialSet.getTargetDataset().getMatrix().minus(
                compute.apply(randomFromTransformationFiducialSet.getSourceDataset()).getMatrix()
        ));
        error.getMatrix().print(1,5);
        error.getBarycentre().getMatrix().print(1,5);
    }

    @Test(enabled = false)
    void LS_leave_one_out() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(range.length);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n, range, isotropicCovariance);

        Dataset error = new Dataset(3);

        for(int i = 0; i < randomFromTransformationFiducialSet.getN(); i++) {
            Point targetRemovedPoint = randomFromTransformationFiducialSet.getTargetDataset().removePoint(0);
            Point sourceRemovedPoint = randomFromTransformationFiducialSet.getSourceDataset().removePoint(0);
            randomFromTransformationFiducialSet = new FiducialSet(
                    randomFromTransformationFiducialSet.getSourceDataset(),
                    randomFromTransformationFiducialSet.getTargetDataset()
            );
            AffineTransformation compute = affineTransformationComputer.compute(randomFromTransformationFiducialSet);
            Matrix minus = targetRemovedPoint.getMatrix().minus(
                    compute.apply(sourceRemovedPoint).getMatrix()
            );
            error.addPoint(new Point(minus));
            randomFromTransformationFiducialSet.getTargetDataset().addPoint(targetRemovedPoint);
            randomFromTransformationFiducialSet.getSourceDataset().addPoint(sourceRemovedPoint);
            randomFromTransformationFiducialSet = new FiducialSet(
                    randomFromTransformationFiducialSet.getSourceDataset(),
                    randomFromTransformationFiducialSet.getTargetDataset()
            );
        }

        error.getMatrix().print(1,5);
        error.getBarycentre().getMatrix().print(1,5);
    }

    @Test(enabled = false)
    void TLS() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(range.length);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n, range, isotropicCovariance);
        tlsAffineTransformationComputer.compute(randomFromTransformationFiducialSet);
    }

    @Test(enabled = false)
    void EKF() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(range.length);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n, range, isotropicCovariance);

        Similarity leastSquareEstimate = rigidTransformationComputer.compute(randomFromTransformationFiducialSet);
        KalmanFilterState kalmanFilterState = new KalmanFilterState(leastSquareEstimate.getMatrixRight(), Matrix.identity(12, 12));
        for(int i = 0; i < 10000; i++) {
            kalmanFilterState = extendedKalmanFilter.run(randomFromTransformationFiducialSet, kalmanFilterState.getEstimate(), kalmanFilterState.getCovariance());
        }

        kalmanFilterState.getEstimate().print(1,5);
        kalmanFilterState.getCovariance().print(1,5);
    }

    @Test(enabled = false)
    void covariance() {
        double angle = 38;
        int n = 15;
        int nFiducial = 50;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(range.length);
        simpleRotationTransformation.getMatrixRight().print(1,5);
        Matrix M = new Matrix(n, 12);
        for(int i = 0; i < n; i++) {
            FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, nFiducial, range, isotropicCovariance);
            Similarity computedTransformation = rigidTransformationComputer.compute(randomFromTransformationFiducialSet);

            for(int u = 0; u < computedTransformation.getR().getRowDimension(); u++) {
                for(int v = 0; v < computedTransformation.getR().getColumnDimension(); v++) {
                    M.set(i, u * computedTransformation.getR().getRowDimension() + v, computedTransformation.getHomogeneousMatrix().get(u, v));
                }
            }

            for(int t = 0; t < computedTransformation.getT().getRowDimension(); t++) {
                M.set(
                        i,
                    computedTransformation.getR().getRowDimension() * computedTransformation.getR().getRowDimension() + t,
                        computedTransformation.getHomogeneousMatrix().get(
                             t, computedTransformation.getHomogeneousMatrix().getColumnDimension() - 1
                        )
                );
            }
        }

        new Dataset(M).getBarycentre().getMatrix().print(1,5);
        Matrix covariance = covarianceMatrixComputer.compute(M);
        covariance.print(1,5);

        Matrix covarianceEstimate = errorComputer.getCovarianceEstimate(new TransformationSchema(
                testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, nFiducial, range, isotropicCovariance),
                TransformationType.RIGID,
                null,
                null
        ));
        covarianceEstimate.print(1, 5);
    }
}