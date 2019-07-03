package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.RoiFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import static plugins.perrine.easyclemv0.EasyCLEMv0.Colortab;

public class RoiUpdater {

    private DatasetFactory datasetFactory = new DatasetFactory();
    private RoiFactory roiFactory = new RoiFactory();

    public void updateRoi(Dataset dataset, Sequence sequence) {
        Dataset pixelDataset = datasetFactory.toPixel(dataset, sequence);
        sequence.removeAllROI();
        for(int i = 0; i < pixelDataset.getN(); i++) {
            ROI roi = roiFactory.getFrom(pixelDataset.getPoint(i));
            roi.setName("Point " + (i + 1));
            roi.setColor(Colortab[i % Colortab.length]);
            roi.setStroke(6);
            sequence.addROI(roi);
        }
    }
}
