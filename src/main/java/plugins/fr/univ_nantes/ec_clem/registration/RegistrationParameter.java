package plugins.fr.univ_nantes.ec_clem.registration;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;

public class RegistrationParameter {
    private Transformation transformation;
    private Matrix noiseCovariance;

    public RegistrationParameter(Transformation transformation, Matrix noiseCovariance) {
        this.transformation = transformation;
        this.noiseCovariance = noiseCovariance;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public Matrix getNoiseCovariance() {
        return noiseCovariance;
    }
}
