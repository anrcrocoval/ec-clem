package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import Jama.Matrix;

public class EulerZYXParameters {

    private double alpha;
    private double beta;
    private double gamma;

    public EulerZYXParameters(Matrix R) {
        alpha = Math.atan2(R.get(1, 0), R.get(0, 0));
        beta = Math.atan2(-1 * R.get(2, 0), Math.sqrt(Math.pow(R.get(0, 0), 2) + Math.pow(R.get(1, 0), 2)));
        gamma = Math.atan2(R.get(2, 1), R.get(2, 2));
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public double getGamma() {
        return gamma;
    }
}
