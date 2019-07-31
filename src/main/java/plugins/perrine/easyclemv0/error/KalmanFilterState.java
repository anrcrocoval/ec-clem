package plugins.perrine.easyclemv0.error;

import Jama.Matrix;

public class KalmanFilterState {
    private Matrix estimate;
    private Matrix covariance;

    public KalmanFilterState(Matrix estimate, Matrix covariance) {
        this.estimate = estimate;
        this.covariance = covariance;
    }

    public Matrix getEstimate() {
        return estimate;
    }

    public Matrix getCovariance() {
        return covariance;
    }
}
