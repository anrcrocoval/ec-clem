package plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.dimension2;

import Jama.Matrix;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.JacobianMatrix;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.JacobianMatrix;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.registration.RegistrationParameter;
import plugins.perrine.ec_clem.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class JacobianMatrix2D implements JacobianMatrix {

    private RotationParameters2D rotationParameters2D;

    @Inject
    public JacobianMatrix2D(RotationParameters2D rotationParameters2D) {
        this.rotationParameters2D = rotationParameters2D;
    }

    public Matrix getJacobian(Point z, RegistrationParameter registrationParameter) {
        double theta = rotationParameters2D.getTheta((Similarity) registrationParameter.getTransformation());
        return new Matrix(new double[][] {
            { -1, 0, z.get(0) * sin(theta) + z.get(1) * cos(theta) },
            { 0, -1, -1 * z.get(0) * cos(theta) + z.get(1) * sin(theta) }
        });
    }
}
