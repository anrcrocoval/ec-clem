package plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.dimension3;

import Jama.Matrix;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.JacobianMatrix;
import plugins.perrine.ec_clem.ec_clem.registration.RegistrationParameter;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.JacobianMatrix;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.dimension2.RotationParameters2D;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.registration.RegistrationParameter;
import plugins.perrine.ec_clem.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class JacobianMatrix3D implements JacobianMatrix {

    private RotationParameters3D rotationParameters3D;

    @Inject
    public JacobianMatrix3D(RotationParameters3D rotationParameters3D) {
        this.rotationParameters3D = rotationParameters3D;
    }

    public Matrix getJacobian(Point z, RegistrationParameter registrationParameter) {
        double[] eulerParameters = rotationParameters3D.getZYZEulerParameters((Similarity) registrationParameter.getTransformation());
        double alpha = eulerParameters[0];
        double beta = eulerParameters[1];
        double gamma = eulerParameters[2];
        return new Matrix(new double[][] {
            {
                -1,
                0,
                0,
                z.get(0) * (sin(alpha) * cos(beta) * cos(gamma) + cos(alpha) * sin(gamma))
                    + z.get(1) * (-sin(alpha) * cos(beta) * sin(gamma) + cos(alpha) * cos(gamma))
                    + z.get(2) * (sin(alpha) * sin(beta)),
                z.get(0) * (sin(beta) * cos(alpha) * cos(gamma))
                    + z.get(1) * (-sin(beta) * cos(alpha) * sin(gamma))
                    + z.get(2) * (-cos(alpha) * cos(beta)),
                z.get(0) * (sin(gamma) * cos(alpha) * cos(beta) + sin(alpha) * cos(gamma))
                    + z.get(1) * (cos(alpha) * cos(beta) * cos(gamma) - sin(alpha) * sin(gamma))
            },
            {
                0,
                -1,
                0,
                z.get(0) * (-cos(alpha) * cos(beta) * cos(gamma) + sin(alpha) * sin(gamma))
                    + z.get(1) * (cos(alpha) * cos(beta) * sin(gamma) + sin(alpha) * cos(gamma))
                    + z.get(2) * (-cos(alpha) * sin(beta)),
                z.get(0) * (sin(alpha) * sin(beta) * cos(gamma))
                    + z.get(1) * (sin(alpha) * -sin(beta) * sin(gamma))
                    + z.get(2) * (-sin(alpha) * cos(beta)),
                z.get(0) * (sin(alpha) * cos(beta) * sin(gamma) - cos(alpha) * cos(gamma))
                    + z.get(1) * (sin(alpha) * cos(beta) * cos(gamma) - cos(alpha) * -sin(gamma))
            },
            {
                0,
                0,
                -1,
                0,
                z.get(0) * (cos(beta * cos(gamma)))
                    + z.get(1) * (-cos(beta) * sin(gamma))
                    + z.get(2) * (sin(beta)),
                z.get(0) * (sin(beta) * -sin(gamma))
                    + z.get(1) * (-sin(beta) * cos(gamma))
            },
        });
    }
}
