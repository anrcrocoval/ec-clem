package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.RoiFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import static plugins.perrine.easyclemv0.EasyCLEMv0.Colortab;

public class RoiUpdater {

    private RoiFactory roiFactory = new RoiFactory();

    public void updateRoi(Dataset dataset, Sequence sequence) {
        sequence.removeAllROI();
        for(int i = 0; i < dataset.getN(); i++) {
            ROI roi = roiFactory.getFrom(dataset.getPoint(i));
            roi.setName("Point " + (i + 1));
            roi.setColor(Colortab[i % Colortab.length]);
            roi.setStroke(6);
            sequence.addROI(roi);
        }
    }
}
