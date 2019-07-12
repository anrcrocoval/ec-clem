package plugins.perrine.easyclemv0.model.transformation;

import Jama.Matrix;

public class Similarity extends AffineTransformation {

    private Matrix R;
    private Matrix S;

    public Similarity(Matrix R, Matrix T, Matrix S) {
        super(S.times(R), T);
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
