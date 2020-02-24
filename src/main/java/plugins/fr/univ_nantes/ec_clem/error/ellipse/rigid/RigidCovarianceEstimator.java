package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.error.ellipse.CovarianceEstimator;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.registration.RegistrationParameter;
import plugins.fr.univ_nantes.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;

public class RigidCovarianceEstimator implements CovarianceEstimator {

    private RegistrationParameterFactory transformationFactory;
    private InverseFisherInformationMatrixEstimatorFactory inverseFisherInformationMatrixEstimatorFactory;
    private JacobianMatrixFactory jacobianMatrixFactory;

    @Inject
    public RigidCovarianceEstimator(
        RegistrationParameterFactory transformationFactory,
        InverseFisherInformationMatrixEstimatorFactory inverseFisherInformationMatrixEstimatorFactory,
        JacobianMatrixFactory jacobianMatrixFactory
    ) {
        this.transformationFactory = transformationFactory;
        this.inverseFisherInformationMatrixEstimatorFactory = inverseFisherInformationMatrixEstimatorFactory;
        this.jacobianMatrixFactory = jacobianMatrixFactory;
    }

    @Override
    public Matrix getCovariance(TransformationSchema transformationSchema, Point zSource) {
        RegistrationParameter from = transformationFactory.getFrom(transformationSchema);
        Matrix lambda = from.getNoiseCovariance();
        Matrix sigma = inverseFisherInformationMatrixEstimatorFactory.getFrom(zSource.getDimension())
            .getInverseFisherInformationMatrix(transformationSchema.getFiducialSet(), from);
        Matrix jacobian = jacobianMatrixFactory.getFrom(zSource.getDimension())
            .getJacobian(zSource, from);
        Matrix E = jacobian.times(sigma).times(jacobian.transpose());
        return E.plus(lambda);
    }
}
