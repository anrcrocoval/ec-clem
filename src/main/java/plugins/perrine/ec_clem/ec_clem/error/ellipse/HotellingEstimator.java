package plugins.perrine.ec_clem.ec_clem.error.ellipse;

import org.apache.commons.math3.distribution.FDistribution;
import plugins.perrine.ec_clem.ec_clem.fiducialset.FiducialSet;
import javax.inject.Inject;

public class HotellingEstimator {

    @Inject
    public HotellingEstimator() {}

    public double getFrom(FiducialSet fiducialSet, double alpha) {
        FDistribution fisher = new FDistribution(
            fiducialSet.getTargetDataset().getDimension(),
            fiducialSet.getN() - fiducialSet.getSourceDataset().getDimension() - fiducialSet.getTargetDataset().getDimension()
        );

        return  ((double) (
            fiducialSet.getTargetDataset().getDimension()
                * (fiducialSet.getN() - fiducialSet.getSourceDataset().getDimension() - 1)
        ) / (double) (
            fiducialSet.getN() - fiducialSet.getSourceDataset().getDimension()
                - fiducialSet.getTargetDataset().getDimension()
        )) * fisher.inverseCumulativeProbability(alpha);
    }
}
