package plugins.perrine.easyclemv0.model.transformation;

import Jama.Matrix;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;

public class AffineTransformation implements Transformation {
    protected Matrix A;
    protected Matrix T;

    public AffineTransformation(Matrix A, Matrix T) {
        this.A = A;
        this.T = T;
    }

    public Point apply(Point point) {
        return new Point(A.times(point.getMatrix()).plus(T));
    }

    // A then T
    public Dataset apply(Dataset dataset) {
        Matrix M = (getMatrix().times(dataset.getHomogeneousMatrix().transpose())).transpose();
        M = M.getMatrix(0, M.getRowDimension() - 1, 0, M.getColumnDimension() - 2);
        return new Dataset(M);
    }

    public Matrix getMatrix() {
        Matrix M = new Matrix(A.getRowDimension() + 1, A.getColumnDimension() + 1, 0);
        for(int i = 0; i < A.getRowDimension(); i++) {
            for(int j = 0; j < A.getColumnDimension(); j++) {
                M.set(i, j, A.get(i, j));
            }
        }
        for(int i = 0; i < T.getRowDimension(); i++) {
            M.set(i, M.getColumnDimension() - 1, T.get(i, 0));
        }
        M.set(M.getRowDimension() - 1, M.getColumnDimension() - 1, 1);
        return M;
    }
}
