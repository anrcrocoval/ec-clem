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
    private InverseFisherInformationMatrix2DEstimator inverseFisherInformationMatrix2DEstimator;
    private JacobianMatrix2D jacobianMatrix2D;

    @Inject
    public RigidCovarianceEstimator(
        RegistrationParameterFactory transformationFactory,
        InverseFisherInformationMatrix2DEstimator inverseFisherInformationMatrix2DEstimator,
        JacobianMatrix2D jacobianMatrix2D
    ) {
        this.transformationFactory = transformationFactory;
        this.inverseFisherInformationMatrix2DEstimator = inverseFisherInformationMatrix2DEstimator;
        this.jacobianMatrix2D = jacobianMatrix2D;
    }

    @Override
    public Matrix getCovariance(TransformationSchema transformationSchema, Point zSource) {
        RegistrationParameter from = transformationFactory.getFrom(transformationSchema);
        Matrix lambda = from.getNoiseCovariance();
        Matrix sigma = inverseFisherInformationMatrix2DEstimator.getInverseFisherInformationMatrix(transformationSchema.getFiducialSet(), from);
        Matrix jacobian = jacobianMatrix2D.getJacobian(zSource, from);
        Matrix E = jacobian.times(sigma).times(jacobian.transpose());
        return E.plus(lambda);
    }
}
