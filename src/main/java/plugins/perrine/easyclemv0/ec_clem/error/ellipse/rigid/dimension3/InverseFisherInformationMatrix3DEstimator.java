package plugins.perrine.easyclemv0.ec_clem.error.ellipse.rigid.dimension3;

import Jama.Matrix;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.rigid.InverseFisherInformationMatrixEstimator;
import plugins.perrine.easyclemv0.ec_clem.registration.RegistrationParameter;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.rigid.InverseFisherInformationMatrixEstimator;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.ec_clem.registration.RegistrationParameter;
import plugins.perrine.easyclemv0.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class InverseFisherInformationMatrix3DEstimator extends InverseFisherInformationMatrixEstimator {

    private RotationParameters3D rotationParameters3D;

    @Inject
    public InverseFisherInformationMatrix3DEstimator(MatrixUtil matrixUtil, RotationParameters3D rotationParameters3D) {
        super(matrixUtil);
        this.rotationParameters3D = rotationParameters3D;
    }

    @Override
    protected int getNParameters() {
        return 6;
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
                { 0 },
                { 0 }
            });
            case 1: return new Matrix(new double[][] {
                { 0 },
                { -1 },
                { 0 }
            });
            case 2: return new Matrix(new double[][] {
                { 0 },
                { 0 },
                { -1 }
            });
            case 3: {
                Point z = fiducialSet.getSourceDataset().getPoint(n);
                double[] eulerParameters = rotationParameters3D.getZYZEulerParameters((Similarity) registrationParameter.getTransformation());
                double alpha = eulerParameters[0];
                double beta = eulerParameters[1];
                double gamma = eulerParameters[2];
                return new Matrix(new double[][] {
                    { z.get(0) * (sin(alpha) * cos(beta) * cos(gamma) + cos(alpha) * sin(gamma))
                        + z.get(1) * (-sin(alpha) * cos(beta) * sin(gamma) + cos(alpha) * cos(gamma))
                        + z.get(2) * (sin(alpha) * sin(beta))
                    },
                    { z.get(0) * (-cos(alpha) * cos(beta) * cos(gamma) + sin(alpha) * sin(gamma))
                        + z.get(1) * (cos(alpha) * cos(beta) * sin(gamma) + sin(alpha) * cos(gamma))
                        + z.get(2) * (-cos(alpha) * sin(beta))
                    },
                    { 0 }
                });
            }
            case 4: {
                Point z = fiducialSet.getSourceDataset().getPoint(n);
                double[] eulerParameters = rotationParameters3D.getZYZEulerParameters((Similarity) registrationParameter.getTransformation());
                double alpha = eulerParameters[0];
                double beta = eulerParameters[1];
                double gamma = eulerParameters[2];
                return new Matrix(new double[][]{
                    {z.get(0) * (sin(beta) * cos(alpha) * cos(gamma))
                        + z.get(1) * (-sin(beta) * cos(alpha) * sin(gamma))
                        + z.get(2) * (-cos(alpha) * cos(beta))
                    },
                    {z.get(0) * (sin(alpha) * sin(beta) * cos(gamma))
                        + z.get(1) * (sin(alpha) * -sin(beta) * sin(gamma))
                        + z.get(2) * (-sin(alpha) * cos(beta))
                    },
                    {z.get(0) * (cos(beta * cos(gamma)))
                        + z.get(1) * (-cos(beta) * sin(gamma))
                        + z.get(2) * (sin(beta))
                    }
                });
            }
            case 5: {
                Point z = fiducialSet.getSourceDataset().getPoint(n);
                double[] eulerParameters = rotationParameters3D.getZYZEulerParameters((Similarity) registrationParameter.getTransformation());
                double alpha = eulerParameters[0];
                double beta = eulerParameters[1];
                double gamma = eulerParameters[2];
                return new Matrix(new double[][] {
                    { z.get(0) * (sin(gamma) * cos(alpha) * cos(beta) + sin(alpha) * cos(gamma))
                        + z.get(1) * (cos(alpha) * cos(beta) * cos(gamma) - sin(alpha) * sin(gamma))
                    },
                    { z.get(0) * (sin(alpha) * cos(beta) * sin(gamma) - cos(alpha) * cos(gamma))
                        + z.get(1) * (sin(alpha) * cos(beta) * cos(gamma) - cos(alpha) * -sin(gamma))
                    },
                    { z.get(0) * (sin(beta) * -sin(gamma))
                            + z.get(1) * (-sin(beta) * cos(gamma))
                    }
                });
            }
            default: throw new RuntimeException("Only 3 parameters in rigid 2D");
        }
    }

    @Override
    protected Matrix getHessianX(FiducialSet fiducialSet, RegistrationParameter registrationParameter, int n, int i, int j) {
        if(i == 0 || i == 1 || i == 2 || j == 0 || j == 1 || j == 2) {
            return new Matrix(new double[][] {
                { 0 },
                { 0 },
                { 0 }
            });
        }
        Point z = fiducialSet.getSourceDataset().getPoint(n);
        double[] eulerParameters = rotationParameters3D.getZYZEulerParameters((Similarity) registrationParameter.getTransformation());
        double alpha = eulerParameters[0];
        double beta = eulerParameters[1];
        double gamma = eulerParameters[2];

        if(i == 3 && j == 3) {
            return new Matrix(new double[][] {
                {z.get(0) * (cos(alpha) * cos(beta) * cos(gamma) - sin(alpha) * sin(gamma))
                    + z.get(1) * (-cos(alpha) * cos(beta) * sin(gamma) - sin(alpha) * cos(gamma))
                    + z.get(2) * (cos(alpha) * sin(beta))
                },
                {z.get(0) * (sin(alpha) * cos(beta) * cos(gamma) + cos(alpha) * sin(gamma))
                    + z.get(1) * (-sin(alpha) * cos(beta) * sin(gamma) + cos(alpha) * cos(gamma))
                    + z.get(2) * (sin(alpha) * sin(beta))
                },
                { 0 }
            });
        }

        if((i == 3 && j == 4) || (i == 4 && j == 3)) {
            return new Matrix(new double[][] {
                {
                    z.get(0) * (-sin(alpha) * sin(beta) * cos(gamma) + cos(alpha) * sin(gamma))
                        + z.get(1) * (sin(alpha) * sin(beta) * sin(gamma) + cos(alpha) * cos(gamma))
                        + z.get(2) * (sin(alpha) * cos(beta))
                },
                {
                    z.get(0) * (cos(alpha) * sin(beta) * cos(gamma) + sin(alpha) * sin(gamma))
                        + z.get(1) * (-cos(alpha) * sin(beta) * sin(gamma) + sin(alpha) * cos(gamma))
                        + z.get(2) * (-cos(alpha) * cos(beta))
                },
                { 0 }
            });
        }

        if((i == 3 && j == 5) || (i == 5 && j == 3)) {
            return new Matrix(new double[][] {
                {
                    z.get(0) * (-sin(alpha) * cos(beta) * sin(gamma) + cos(alpha) * cos(gamma))
                        + z.get(1) * (-sin(alpha) * cos(beta) * cos(gamma) - cos(alpha) * sin(gamma))
                },
                {
                    z.get(0) * (cos(alpha) * cos(beta) * sin(gamma) + sin(alpha) * cos(gamma))
                        + z.get(1) * (cos(alpha) * cos(beta) * cos(gamma) - sin(alpha) * sin(gamma))
                },
                { 0 }
            });
        }

        if((i == 4 && j == 4)) {
            return new Matrix(new double[][] {
                {
                    z.get(0) * (cos(alpha) * cos(beta) * cos(gamma))
                        + z.get(1) * (-cos(alpha) * cos(beta) * sin(gamma))
                        + z.get(2) * (cos(alpha) * sin(beta))
                },
                {
                    z.get(0) * (sin(alpha) * cos(beta) * cos(gamma))
                    + z.get(1) * (-sin(alpha) * cos(beta) * sin(gamma))
                    + z.get(2) * (sin(alpha) * sin(beta))
                },
                { z.get(0) * (-sin(beta) * cos(gamma))
                    + z.get(1) * (sin(beta) * sin(gamma))
                    + z.get(2) * (cos(beta))
                }
            });
        }

        if((i == 4 && j == 5) || (i == 5 && j == 4)) {
            return new Matrix(new double[][] {
                {
                    z.get(0) * (-cos(alpha) * sin(beta) * sin(gamma))
                        + z.get(1) * (-cos(alpha) * sin(beta) * cos(gamma))
                },
                {
                    z.get(0) * (-sin(alpha) * sin(beta) * sin(gamma))
                        + z.get(1) * (-sin(alpha) * sin(beta) * cos(gamma))
                },
                { z.get(0) * (-cos(beta) * sin(gamma))
                    + z.get(1) * (-cos(beta) * cos(gamma))
                }
            });
        }

        if((i == 5 && j == 5)) {
            return new Matrix(new double[][] {
                {
                    z.get(0) * (cos(alpha) * cos(beta) * cos(gamma) - sin(alpha) * sin(gamma))
                        + z.get(1) * (-cos(alpha) * cos(beta) * sin(gamma) - sin(alpha) * cos(gamma))
                },
                {
                    z.get(0) * (sin(alpha) * cos(beta) * cos(gamma) + cos(alpha) * sin(gamma))
                        + z.get(1) * (- sin(alpha) * cos(beta) * sin(gamma) + cos(alpha) * cos(gamma))
                },
                { z.get(0) * (-sin(beta) * cos(gamma))
                    + z.get(1) * (sin(beta) * sin(gamma))
                }
            });
        }

        throw new RuntimeException("Cas not implemented");
    }
}
