package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import plugins.perrine.easyclemv0.factory.apache.common.math.RealMatrixFactory;
import plugins.perrine.easyclemv0.factory.jama.MatrixFactory;

import javax.inject.Inject;

public class CovarianceMatrixComputer {

    private RealMatrixFactory realMatrixFactory;
    private MatrixFactory matrixFactory;

    @Inject
    public CovarianceMatrixComputer(RealMatrixFactory realMatrixFactory, MatrixFactory matrixFactory) {
        this.realMatrixFactory = realMatrixFactory;
        this.matrixFactory = matrixFactory;
    }

    public Matrix compute(Matrix M) {
        return matrixFactory.getFrom(
            new Covariance(realMatrixFactory.getFrom(M)).getCovarianceMatrix()
        );
    }
}
