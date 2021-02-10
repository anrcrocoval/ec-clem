package plugins.perrine.ec_clem.ec_clem.fixtures.matrix;

import Jama.Matrix;
import javax.inject.Inject;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TestMatrixFactory {

    @Inject
    public TestMatrixFactory() {}

    public Matrix getZYZ3DRotationMatrix(double alpha, double beta, double gamma) {
        return getZAxis3DRotation(alpha).times(
            getYAxis3DRotation(beta)
        ).times(
            getZAxis3DRotation(gamma)
        );
    }

    private Matrix getZAxis3DRotation(double angle) {
        return new Matrix(new double[][] {
            { cos(angle), -sin(angle), 0 },
            { sin(angle), cos(angle), 0 },
            { 0, 0, 1 }
        });
    }

    private Matrix getYAxis3DRotation(double angle) {
        return new Matrix(new double[][] {
            { cos(angle), 0, sin(angle) },
            { 0, 1, 0 },
            { -sin(angle), 0, cos(angle) }
        });
    }
}
