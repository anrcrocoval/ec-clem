package plugins.perrine.easyclemv0.ec_clem.error.ellipse.rigid;

import Jama.Matrix;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.registration.RegistrationParameter;

public interface JacobianMatrix {
    Matrix getJacobian(Point z, RegistrationParameter registrationParameter);
}
