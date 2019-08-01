package plugins.perrine.easyclemv0.factory.jama;

import Jama.Matrix;
import org.apache.commons.math3.linear.RealMatrix;

import javax.inject.Inject;

public class MatrixFactory {

    @Inject
    public MatrixFactory() {
    }

    public Matrix getFrom(RealMatrix M) {
        return new Matrix(M.getData());
    }
}
