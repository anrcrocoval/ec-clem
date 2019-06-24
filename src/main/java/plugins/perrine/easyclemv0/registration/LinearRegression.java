package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;

public class LinearRegression {

    public Matrix solve(Matrix A, Matrix B) {
        return (A.transpose().times(A)).inverse().times(A.transpose()).times(B);
    }
}
