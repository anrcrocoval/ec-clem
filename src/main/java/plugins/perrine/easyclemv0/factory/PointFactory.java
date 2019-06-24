package plugins.perrine.easyclemv0.factory;

import icy.roi.ROI;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.roi.RoiProcessor;

public class PointFactory {

    private RoiProcessor roiProcessor = new RoiProcessor();

    public Point getFrom(ROI roi) {
        return new Point(roiProcessor.getPointFromRoi(roi));
    }

    public Point getFrom(double ... coordinates) {
        return new Point(coordinates);
    }
}
