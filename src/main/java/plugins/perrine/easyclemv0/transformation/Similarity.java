package plugins.perrine.easyclemv0.transformation;

import Jama.Matrix;

public class Similarity extends AffineTransformation {

    private Matrix R;
    private Matrix S;

    public Similarity(Matrix R, Matrix T, Matrix S) {
        super(R.times(S), T);
        this.R = R;
        this.S = S;
    }

    public Matrix getR() {
        return R;
    }

    public Matrix getT() {
        return T;
    }

    public Matrix getS() {
        return S;
    }
}
