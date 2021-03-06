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
import plugins.perrine.ec_clem.ec_clem.error.CovarianceMatrixComputer;
import plugins.perrine.ec_clem.ec_clem.error.ErrorComputer;
import plugins.perrine.ec_clem.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import plugins.perrine.ec_clem.ec_clem.fixtures.transformation.TestTransformationFactory;
import org.testng.annotations.Test;
import plugins.perrine.ec_clem.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.registration.AffineRegistrationParameterComputer;
import plugins.perrine.ec_clem.ec_clem.registration.RegistrationParameter;
import plugins.perrine.ec_clem.ec_clem.registration.RigidRegistrationParameterComputer;
//import plugins.perrine.ec_clem.registration.TLSAffineRegistrationParameterComputer;
import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.NoiseModel;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationType;
import plugins.perrine.ec_clem.ec_clem.transformation.Similarity;

import javax.inject.Inject;

class IgnoredTest {

    private TestTransformationFactory testTransformationFactory;
    private TestFiducialSetFactory testFiducialSetFactory;
    private RigidRegistrationParameterComputer rigidTransformationComputer;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private ErrorComputer errorComputer;
//    private TLSAffineRegistrationParameterComputer tlsAffineTransformationComputer;
    private AffineRegistrationParameterComputer affineTransformationComputer;

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
    public void setRigidTransformationComputer(RigidRegistrationParameterComputer rigidTransformationComputer) {
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

//    @Inject
//    public void setTlsAffineTransformationComputer(TLSAffineRegistrationParameterComputer tlsAffineTransformationComputer) {
//        this.tlsAffineTransformationComputer = tlsAffineTransformationComputer;
//    }

    @Inject
    public void setAffineTransformationComputer(AffineRegistrationParameterComputer affineTransformationComputer) {
        this.affineTransformationComputer = affineTransformationComputer;
    }

    @Test(enabled = false)
    void LS() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(range.length);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n, range, isotropicCovariance);
        RegistrationParameter compute = affineTransformationComputer.compute(randomFromTransformationFiducialSet);
        Dataset error = new Dataset(randomFromTransformationFiducialSet.getTargetDataset().getMatrix().minus(
                compute.getTransformation().apply(randomFromTransformationFiducialSet.getSourceDataset()).getMatrix()
        ), PointType.FIDUCIAL);
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

        Dataset error = new Dataset(3, PointType.FIDUCIAL);

        for(int i = 0; i < randomFromTransformationFiducialSet.getN(); i++) {
            Point targetRemovedPoint = randomFromTransformationFiducialSet.getTargetDataset().removePoint(0);
            Point sourceRemovedPoint = randomFromTransformationFiducialSet.getSourceDataset().removePoint(0);
            randomFromTransformationFiducialSet = new FiducialSet(
                    randomFromTransformationFiducialSet.getSourceDataset(),
                    randomFromTransformationFiducialSet.getTargetDataset()
            );
            RegistrationParameter compute = affineTransformationComputer.compute(randomFromTransformationFiducialSet);
            Matrix minus = targetRemovedPoint.getMatrix().minus(
                    compute.getTransformation().apply(sourceRemovedPoint).getMatrix()
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

//    @Test(enabled = false)
//    void TLS() {
//        double angle = 38;
//        int n = 15;
//        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(range.length);
//        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
//        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n, range, isotropicCovariance);
//        tlsAffineTransformationComputer.compute(randomFromTransformationFiducialSet);
//    }

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
            Similarity computedTransformation = (Similarity) rigidTransformationComputer.compute(randomFromTransformationFiducialSet).getTransformation();

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

        new Dataset(M, PointType.FIDUCIAL).getBarycentre().getMatrix().print(1,5);
        Matrix covariance = covarianceMatrixComputer.compute(M);
        covariance.print(1,5);

        Matrix covarianceEstimate = errorComputer.getCovarianceEstimate(new TransformationSchema(
                testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, nFiducial, range, isotropicCovariance),
                TransformationType.RIGID,
                NoiseModel.ISOTROPIC,
                null,
                null
        ));
        covarianceEstimate.print(1, 5);
    }
}