package plugins.perrine.easyclemv0.test;

import Jama.Matrix;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.error.*;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.test.fiducialset.TestFiducialSetFactory;
import plugins.perrine.easyclemv0.test.transformation.TestTransformationFactory;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.schema.TransformationType;
import plugins.perrine.easyclemv0.transformation.Similarity;
import plugins.perrine.easyclemv0.registration.RigidTransformationComputer;
import javax.inject.Inject;

@Disabled
class IgnoredTest {

    private TestTransformationFactory testTransformationFactory;
    private TestFiducialSetFactory testFiducialSetFactory;
    private RigidTransformationComputer rigidTransformationComputer;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private ErrorComputer errorComputer;
    private ExtendedKalmanFilter extendedKalmanFilter;

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

    @Test
    void EKF() {
        double angle = 38;
        int n = 15;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        simpleRotationTransformation.getHomogeneousMatrix().print(1,5);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomAndNoisyFromTransformation(simpleRotationTransformation, n);

        Similarity leastSquareEstimate = rigidTransformationComputer.compute(randomFromTransformationFiducialSet);
        KalmanFilterState kalmanFilterState = new KalmanFilterState(leastSquareEstimate.getMatrixRight(), Matrix.identity(12, 12));
        for(int i = 0; i < 10; i++) {
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
