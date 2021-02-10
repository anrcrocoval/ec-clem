package plugins.perrine.easyclemv0.ec_clem.error.ellipse.affine;

import Jama.Matrix;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.CovarianceEstimator;
import plugins.perrine.easyclemv0.ec_clem.registration.RegistrationParameter;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.CovarianceEstimator;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.ec_clem.registration.RegistrationParameter;
import plugins.perrine.easyclemv0.ec_clem.transformation.RegistrationParameterFactory;
import plugins.perrine.easyclemv0.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;

public class AffineCovarianceEstimator implements CovarianceEstimator {

    private MatrixUtil matrixUtil;
    private RegistrationParameterFactory transformationFactory;

    @Inject
    public AffineCovarianceEstimator(RegistrationParameterFactory transformationFactory, MatrixUtil matrixUtil) {
        this.transformationFactory = transformationFactory;
        this.matrixUtil = matrixUtil;
    }

    @Override
    public Matrix getCovariance(TransformationSchema transformationSchema, Point zSource) {
        Matrix hSource = new Matrix(zSource.getDimension() + 1, 1, 1);
        hSource.setMatrix(1, hSource.getRowDimension() - 1, 0, 0, zSource.getMatrix());
        Matrix M = (hSource.transpose()).times(
            matrixUtil.pseudoInverse(
                (transformationSchema.getFiducialSet().getSourceDataset().getHomogeneousMatrixLeft().transpose())
                    .times(transformationSchema.getFiducialSet().getSourceDataset().getHomogeneousMatrixLeft())
            )
        ).times(hSource);
        assert M.getRowDimension() == 1;
        assert M.getColumnDimension() == 1;
        double coeff = (
            M.get(0, 0) + 1
        );
        assert coeff != 0;

        RegistrationParameter from = transformationFactory.getFrom(transformationSchema);

        return from.getNoiseCovariance().times(coeff);
    }
}
