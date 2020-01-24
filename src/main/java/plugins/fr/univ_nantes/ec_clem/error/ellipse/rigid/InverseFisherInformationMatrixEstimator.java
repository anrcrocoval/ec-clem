package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.RegistrationParameter;

public abstract class InverseFisherInformationMatrixEstimator {

    private MatrixUtil matrixUtil;

    public InverseFisherInformationMatrixEstimator(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    public Matrix getInverseFisherInformationMatrix(FiducialSet fiducialSet, RegistrationParameter registrationParameter) {
        Matrix noiseCovarianceInv = matrixUtil.pseudoInverse(registrationParameter.getNoiseCovariance());
        Matrix H = new Matrix(getNParameters(), getNParameters(), 0);
        for(int i = 0; i < getNParameters(); i++) {
            for(int j = 0; j <= i; j++) {
                double Hij = 0;
                for(int n = 0; n < fiducialSet.getN(); n++) {
                    Hij += (
                        (
                            noiseCovarianceInv
                            .times(2)
                            .times(getGradientX(fiducialSet, registrationParameter, n, j))
                        ).transpose().times(getGradientX(fiducialSet, registrationParameter, n, i))
                    ).plus(
                        (
                            noiseCovarianceInv
                            .times(2)
                            .times(getX(fiducialSet, registrationParameter, n))
                        ).transpose().times(getHessianX(fiducialSet, registrationParameter, n, i, j))
                    ).get(0, 0);
                }
                H.set(i, j, Hij / 2d);
                H.set(j, i, Hij / 2d);
            }
        }
        return matrixUtil.pseudoInverse(H);
    }

    protected abstract int getNParameters();
    protected abstract Matrix getX(FiducialSet fiducialSet, RegistrationParameter registrationParameter, int n);
    protected abstract Matrix getGradientX(FiducialSet fiducialSet, RegistrationParameter registrationParameter, int n, int i);
    protected abstract Matrix getHessianX(FiducialSet fiducialSet, RegistrationParameter registrationParameter, int n, int i, int j);
}
