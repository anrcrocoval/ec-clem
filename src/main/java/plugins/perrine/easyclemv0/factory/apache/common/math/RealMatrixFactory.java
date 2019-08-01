package plugins.perrine.easyclemv0.factory.apache.common.math;

import Jama.Matrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javax.inject.Inject;

public class RealMatrixFactory {

    @Inject
    public RealMatrixFactory() {
    }

    public RealMatrix getFrom(Matrix M) {
        return MatrixUtils.createRealMatrix(M.getArray());
    }
}
