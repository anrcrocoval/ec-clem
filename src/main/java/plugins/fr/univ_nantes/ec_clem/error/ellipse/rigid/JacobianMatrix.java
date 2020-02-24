package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.registration.RegistrationParameter;

public interface JacobianMatrix {
    Matrix getJacobian(Point z, RegistrationParameter registrationParameter);
}
