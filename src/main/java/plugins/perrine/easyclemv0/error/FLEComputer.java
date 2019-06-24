package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import plugins.perrine.easyclemv0.model.Dataset;

public class FLEComputer {

    /**
     *
     * @return max error between point localisation on registered image, or
     *         200nm (for fluo resolution) OR the pixel size by default if no
     *         point pair. Should be expressed in nm considered as the FLE
     *         (fiducial localization error)
     *
     */
    public double maxdifferrorinnm(Dataset sourceDataset, Dataset targetDataset) {
        // the min localization error is one pixel or the resolution of fluorescence
        if (sourceDataset == null) {
            System.err.println("Please initialize EasyClem first by pressing the Play button");
            return 0.0;
        }

        if (sourceDataset.getN() < 5) {
            // then the points are perfectly
            // registered which may be a non sense
            // from FLE,
            // we then assume an error of 20 pixels
//            double error = Math.max(sourcePixelSizeX, targetPixelSizeY);
            double error = 20 * 1000; // in nm, was in um
            error = Math.max(200, error);
            error = Math.min(1000, error);
            return error;
        }

        double error = 0;
        Matrix minus = targetDataset.getMatrix().minus(sourceDataset.getMatrix());
        for(int i = 0; i < sourceDataset.getN(); i++) {
            error += minus.getMatrix(i, i, 0, minus.getColumnDimension() - 1).norm2();
        }
        error = error / sourceDataset.getN();

        return Math.max(error, 200);
    }

    public double getExpectedSquareFLE(double expectedSquareFRE, int n) {
        return expectedSquareFRE / (1 - (1 / (2 * (double) n)));
    }
}
