package plugins.perrine.easyclemv0.model;

import Jama.Matrix;

public class AffineTransformation {
    protected Matrix A;
    protected Matrix T;

    public AffineTransformation(Matrix M) {
        this(
            M.getMatrix(0, M.getRowDimension() - 2, 0, M.getColumnDimension() - 2),
            M.getMatrix(0, M.getRowDimension() - 2, M.getColumnDimension() - 1, M.getColumnDimension() - 1)
        );
    }

    public AffineTransformation(Matrix A, Matrix T) {
        this.A = A;
        this.T = T;
    }

    public Point apply(Point point) {
        return new Point(A.times(point.getmatrix()).plus(T));
    }

    public Dataset apply(Dataset dataset) {
        Matrix M = (dataset.getHomogeneousMatrix().times(getMatrix()));
        return new Dataset(M.getArray());
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

    public AffineTransformation inverse() {
        Matrix iA = Matrix.identity(A.getRowDimension() + 1, A.getColumnDimension() + 1);
        iA.setMatrix(0, A.getRowDimension() - 1, 0, A.getColumnDimension() - 1, A.inverse());

        Matrix iT = Matrix.identity(A.getRowDimension() + 1, A.getColumnDimension() + 1);
        for(int i = 0; i < T.getRowDimension(); i++) {
            iT.set(i, iT.getColumnDimension() - 1, T.get(i, 0) * -1);
        }

        return new AffineTransformation(
            iA.times(iT)
        );
    }
}
