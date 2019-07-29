package plugins.perrine.easyclemv0.factory.jama;

import Jama.Matrix;
import org.apache.commons.math3.linear.RealMatrix;

public class MatrixFactory {

    public Matrix getFrom(RealMatrix M) {
        return new Matrix(M.getData());
    }
}
