package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.type.point.Point5D;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.model.Transformation;

import java.util.ArrayList;

public class RoiUpdater {

    private RoiProcessor roiProcessor = new RoiProcessor();

    public void updateRoi(Dataset dataset, Sequence sequence) {
        ArrayList<ROI> listfiducials = sequence.getROIs();
        roiProcessor.sort(listfiducials);
        int i = -1;
        for (ROI roi : listfiducials) {
            i++;
            Point5D position = roi.getPosition5D();
            position.setX(dataset.getMatrix().get(i, 0));
            position.setY(dataset.getMatrix().get(i, 1));
            position.setZ(dataset.getMatrix().get(i, 2));
            roi.setPosition5D(position);
            System.out.println(roi.getName() + " " + dataset.getMatrix().get(i, 0) + " " + dataset.getMatrix().get(i, 1) + " " + dataset.getMatrix().get(i, 2));
        }
    }

}
