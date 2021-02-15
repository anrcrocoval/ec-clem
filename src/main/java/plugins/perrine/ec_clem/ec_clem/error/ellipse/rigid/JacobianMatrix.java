package plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid;

import Jama.Matrix;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.registration.RegistrationParameter;

public interface JacobianMatrix {
    Matrix getJacobian(Point z, RegistrationParameter registrationParameter);
}
