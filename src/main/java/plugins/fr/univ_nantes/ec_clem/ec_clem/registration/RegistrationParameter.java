package plugins.fr.univ_nantes.ec_clem.ec_clem.registration;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Transformation;

public class RegistrationParameter {
    private Transformation transformation;
    private Matrix noiseCovariance;
    private double logLikelihood;

    public RegistrationParameter(
        Transformation transformation,
        Matrix noiseCovariance,
        double logLikelihood
    ) {
        this.transformation = transformation;
        this.noiseCovariance = noiseCovariance;
        this.logLikelihood = logLikelihood;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public Matrix getNoiseCovariance() {
        return noiseCovariance;
    }

    public double getLogLikelihood() {
        return logLikelihood;
    }
}
