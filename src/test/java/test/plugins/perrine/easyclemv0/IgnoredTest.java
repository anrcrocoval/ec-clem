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
package test.plugins.perrine.easyclemv0;

import Jama.Matrix;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.error.*;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.registration.AffineTransformationComputer;
import plugins.perrine.easyclemv0.registration.TLSAffineTransformationComputer;
import plugins.perrine.easyclemv0.transformation.AffineTransformation;
import test.plugins.perrine.easyclemv0.fiducialset.TestFiducialSetFactory;
import test.plugins.perrine.easyclemv0.transformation.TestTransformationFactory;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.schema.TransformationType;
import plugins.perrine.easyclemv0.transformation.Similarity;
import plugins.perrine.easyclemv0.registration.RigidTransformationComputer;
import javax.inject.Inject;
import java.util.concurrent.*;

@Disabled
class IgnoredTest {

    private TestTransformationFactory testTransformationFactory;
    private TestFiducialSetFactory testFiducialSetFactory;
    private RigidTransformationComputer rigidTransformationComputer;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private ErrorComputer errorComputer;
    private ExtendedKalmanFilter extendedKalmanFilter;
    private TLSAffineTransformationComputer tlsAffineTransformationComputer;
    private AffineTransformationComputer affineTransformationComputer;

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

    @Test
    void LS_varying_noise() {
        double angle = 38;
        int n = 100;
        int iter = 50;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getGaussianAroundCenterOfGravityFromTransformation(
                simpleRotationTransformation, n * n + 1
        );
        Matrix error = new Matrix(iter * n, 4);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CompletionService<Runnable> completionService = new ExecutorCompletionService<>(executorService);
        for(int i = 0; i < iter; i++) {
            final int finalI = i;
            for(int j = 0; j < n; j++) {
                final int finalJ = j;
                completionService.submit(() -> {
                    int nbIter = (finalI + 1) * (finalI + 1);
                    int nbPoints = (finalJ + 1) * (finalJ + 1);
                    Matrix currentError = new Matrix(nbIter, 3);
                    for(int current = 0; current < nbIter; current++) {
                        FiducialSet currentFiducialSet = new FiducialSet(
                                randomFromTransformationFiducialSet.getSourceDataset().clone(),
                                randomFromTransformationFiducialSet.getTargetDataset().clone()
                        );
                        testFiducialSetFactory.addGaussianNoise(
                                currentFiducialSet.getTargetDataset()
                        );
                        Point targetRemovedPoint = currentFiducialSet.getTargetDataset().removePoint(0);
                        Point sourceRemovedPoint = currentFiducialSet.getSourceDataset().removePoint(0);

                        for(int k = n * n; k > nbPoints; k--) {
                            currentFiducialSet.getTargetDataset().removePoint(0);
                            currentFiducialSet.getSourceDataset().removePoint(0);
                        }

                        FiducialSet finalCurrentFiducialSet = new FiducialSet(
                                currentFiducialSet.getSourceDataset(),
                                currentFiducialSet.getTargetDataset()
                        );
                        AffineTransformation compute = affineTransformationComputer.compute(finalCurrentFiducialSet);
                        Matrix minus = randomFromTransformationFiducialSet.getTargetDataset().getPoint(0).getMatrix().minus(
                                new Point(compute.apply(sourceRemovedPoint).getMatrix()).getMatrix()
                        );
                        currentError.setMatrix(current, current, 0, 2, minus.transpose());
                    }

                    Dataset d = new Dataset(currentError);
                    Mean mean = new Mean();
                    mean.clear();
                    Variance variance = new Variance();
                    variance.clear();

                    for(int p = 0; p < d.getN(); p++) {
                        double sumOfSquare = d.getPoint(p).getSumOfSquare();
                        mean.increment(sumOfSquare);
                        variance.increment(sumOfSquare);
                    }

                    error.set(finalI * n + finalJ, 0, nbIter);
                    error.set(finalI * n + finalJ, 1, nbPoints);
                    error.set(finalI * n + finalJ, 2, mean.getResult());
                    error.set(finalI * n + finalJ, 3, variance.getResult());
                }, null);
            }
        }

        for(int i = 0; i < iter; i++) {
            for(int j = 0; j < n; j++) {
                try {
                    completionService.take().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        error.print(1,5);
    }

    @Test
    void LS() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n);
        AffineTransformation compute = affineTransformationComputer.compute(randomFromTransformationFiducialSet);
        Dataset error = new Dataset(randomFromTransformationFiducialSet.getTargetDataset().getMatrix().minus(
                compute.apply(randomFromTransformationFiducialSet.getSourceDataset()).getMatrix()
        ));
        error.getMatrix().print(1,5);
        error.getBarycentre().getMatrix().print(1,5);
    }

    @Test
    void LS_leave_one_out() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n);

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

    @Test
    void TLS() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n);
        tlsAffineTransformationComputer.compute(randomFromTransformationFiducialSet);
    }

    @Test
    void EKF() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n);

        Similarity leastSquareEstimate = rigidTransformationComputer.compute(randomFromTransformationFiducialSet);
        KalmanFilterState kalmanFilterState = new KalmanFilterState(leastSquareEstimate.getMatrixRight(), Matrix.identity(12, 12));
        for(int i = 0; i < 10000; i++) {
            kalmanFilterState = extendedKalmanFilter.run(randomFromTransformationFiducialSet, kalmanFilterState.getEstimate(), kalmanFilterState.getCovariance());
        }

        kalmanFilterState.getEstimate().print(1,5);
        kalmanFilterState.getCovariance().print(1,5);
    }

    @Test
    void covariance() {
        double angle = 38;
        int n = 15;
        int nFiducial = 50;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        simpleRotationTransformation.getMatrixRight().print(1,5);
        Matrix M = new Matrix(n, 12);
        for(int i = 0; i < n; i++) {
            FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, nFiducial);
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
                testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, nFiducial),
                TransformationType.RIGID,
                null,
                null
        ));
        covarianceEstimate.print(1, 5);
    }
}
