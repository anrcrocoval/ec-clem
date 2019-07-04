package plugins.perrine.easyclemv0.model.transformation;

import Jama.Matrix;

public class Similarity extends AffineTransformation {

    private Matrix R;
    private Matrix scale;
    private double scaleValue;

    public Similarity(Matrix R, Matrix T, Matrix scale) {
        super(scale.times(R), T);
        this.R = R;
        this.scale = scale;
    }

    public Similarity(Matrix R, Matrix T, double scale) {
        this(R, T, Matrix.identity(T.getRowDimension(), T.getRowDimension()).times(scale));
        scaleValue = scale;
    }

    public Matrix getR() {
        return R;
    }

    public Matrix getT() {
        return T;
    }

    public double getScale() {
        return scaleValue;
    }
}
