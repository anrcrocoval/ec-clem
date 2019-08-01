package plugins.perrine.easyclemv0.fiducialset.dataset.point;

import icy.roi.ROI;
import plugins.perrine.easyclemv0.roi.RoiProcessor;

import javax.inject.Inject;

public class PointFactory {

    private RoiProcessor roiProcessor;

    @Inject
    public PointFactory(RoiProcessor roiProcessor) {
        this.roiProcessor = roiProcessor;
    }

    public Point getFrom(ROI roi) {
        return new Point(roiProcessor.getPointFromRoi(roi));
    }

    public Point getFrom(double ... coordinates) {
        return new Point(coordinates);
    }
}
