package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import plugins.perrine.easyclemv0.factory.apache.common.math.RealMatrixFactory;
import plugins.perrine.easyclemv0.factory.jama.MatrixFactory;

public class CovarianceMatrixComputer {

    private RealMatrixFactory realMatrixFactory = new RealMatrixFactory();
    private MatrixFactory matrixFactory = new MatrixFactory();
    private Covariance covariance;

    public Matrix compute(Matrix M) {
        covariance = new Covariance(realMatrixFactory.getFrom(M));
        return matrixFactory.getFrom(
            covariance.getCovarianceMatrix()
        );
    }
}
