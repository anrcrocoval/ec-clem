package plugins.perrine.easyclemv0.ec_clem.error.ellipse.rigid.dimension2;

import Jama.Matrix;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.rigid.InverseFisherInformationMatrixEstimator;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.rigid.InverseFisherInformationMatrixEstimator;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.ec_clem.registration.RegistrationParameter;
import plugins.perrine.easyclemv0.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class InverseFisherInformationMatrix2DEstimator extends InverseFisherInformationMatrixEstimator {

    private RotationParameters2D rotationParameters2D;

    @Inject
    public InverseFisherInformationMatrix2DEstimator(MatrixUtil matrixUtil, RotationParameters2D rotationParameters2D) {
        super(matrixUtil);
        this.rotationParameters2D = rotationParameters2D;
    }

    @Override
    protected int getNParameters() {
        return 3;
    }

    @Override
    protected Matrix getX(FiducialSet fiducialSet, RegistrationParameter registrationParameter, int n) {
        return fiducialSet.getTargetDataset().getPoint(n)
            .minus(
                registrationParameter.getTransformation().apply(fiducialSet.getSourceDataset().getPoint(n))
            ).getMatrix();
    }

    @Override
    protected Matrix getGradientX(FiducialSet fiducialSet, RegistrationParameter registrationParameter, int n, int i) {
        switch(i) {
            case 0: return new Matrix(new double[][] {
                    { -1 },
                    { 0 }
                });
            case 1: return new Matrix(new double[][] {
                    { 0 },
                    { -1 }
                });
            case 2: {
                Point z = fiducialSet.getSourceDataset().getPoint(n);
                double theta = rotationParameters2D.getTheta((Similarity) registrationParameter.getTransformation());
                return new Matrix(new double[][] {
                    { z.get(0) * sin(theta) + z.get(1) * cos(theta) },
                    { -1 * z.get(0) * cos(theta) + z.get(1) * sin(theta) }
                });
            }
            default: throw new RuntimeException("Only 3 parameters in rigid 2D");
        }
    }

    @Override
    protected Matrix getHessianX(FiducialSet fiducialSet, RegistrationParameter registrationParameter, int n, int i, int j) {
        if(i == 0 || i == 1 || j == 0 || j == 1) {
            return new Matrix(new double[][] {
                { 0 },
                { 0 }
            });
        }
        assert i == j && i == 2;
        Point z = fiducialSet.getSourceDataset().getPoint(n);
        double theta = rotationParameters2D.getTheta((Similarity) registrationParameter.getTransformation());
        return new Matrix(new double[][] {
            { z.get(0) * cos(theta) - z.get(1) * sin(theta) },
            { z.get(0) * sin(theta) + z.get(1) * cos(theta) }
        });
    }
}
