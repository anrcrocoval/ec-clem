package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.type.point.Point5D;
import plugins.perrine.easyclemv0.factory.RoiFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.model.Transformation;

import java.util.ArrayList;

public class RoiUpdater {

    private RoiProcessor roiProcessor = new RoiProcessor();
    private RoiFactory roiFactory = new RoiFactory();

    public void updateRoi(Dataset dataset, Sequence sequence) {
        sequence.removeAllROI();
        for(int i = 0; i < dataset.getN(); i++) {
            ROI roi = roiFactory.getFrom(dataset.getPoint(i));
            roi.setName("Point " + (sequence.getROICount(ROI.class) + 1));
            sequence.addROI(roi);
        }
    }

}
