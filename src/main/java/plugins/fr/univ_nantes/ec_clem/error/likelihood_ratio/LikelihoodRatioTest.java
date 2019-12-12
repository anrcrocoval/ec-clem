package plugins.fr.univ_nantes.ec_clem.error.likelihood_ratio;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import javax.inject.Inject;

public class LikelihoodRatioTest {

    @Inject
    public LikelihoodRatioTest() {}

    public double test(int degreesOfFreedom, double nestedModelLogLikelihood, double generalModelLogLikelihood) {
        ChiSquaredDistribution chi2 = new ChiSquaredDistribution(degreesOfFreedom);
        double likelihoodRatio = 2 * (generalModelLogLikelihood - nestedModelLogLikelihood);
        return 1 - chi2.cumulativeProbability(likelihoodRatio);
    }
}
