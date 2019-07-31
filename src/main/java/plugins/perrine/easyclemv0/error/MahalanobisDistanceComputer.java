package plugins.perrine.easyclemv0.error;

import Jama.Matrix;

public class MahalanobisDistanceComputer {

    public double compute(Matrix x, Matrix y, Matrix cov) {
        Matrix minus = y.minus(x);
            return Math.sqrt(minus.transpose().times(cov).times(minus).get(0, 0)
        );
    }


}
