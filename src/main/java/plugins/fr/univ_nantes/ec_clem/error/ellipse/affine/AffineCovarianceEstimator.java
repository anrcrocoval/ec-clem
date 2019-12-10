package plugins.fr.univ_nantes.ec_clem.error.ellipse.affine;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.error.ellipse.CovarianceEstimator;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.RegistrationParameter;
import plugins.fr.univ_nantes.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;

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
        double coeff = (
            zSource.getMatrix().transpose().times(
                matrixUtil.pseudoInverse(
                    transformationSchema.getFiducialSet().getSourceDataset().getMatrix().transpose().times(
                        transformationSchema.getFiducialSet().getSourceDataset().getMatrix()
                    )
                )
            ).times(zSource.getMatrix()).get(0, 0) + 1
        );

        RegistrationParameter from = transformationFactory.getFrom(transformationSchema);

        return from.getNoiseCovariance().times(coeff);
    }
}
