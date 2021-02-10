package plugins.fr.univ_nantes.ec_clem.ec_clem.error.likelihood_ratio;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import javax.inject.Inject;

public class LikelihoodRatioTest {

    @Inject
    public LikelihoodRatioTest() {}

    public double test(double degreesOfFreedom, double nestedModelLogLikelihood, double generalModelLogLikelihood) {
        ChiSquaredDistribution chi2 = new ChiSquaredDistribution(degreesOfFreedom);
        double likelihoodRatio = 2d * (generalModelLogLikelihood - nestedModelLogLikelihood);
        return 1d - chi2.cumulativeProbability(likelihoodRatio);
    }
}
