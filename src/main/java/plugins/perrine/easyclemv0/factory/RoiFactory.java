package plugins.perrine.easyclemv0.factory;

import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.type.point.Point5D;
import plugins.kernel.roi.roi2d.ROI2DPoint;
import plugins.kernel.roi.roi3d.ROI3DPoint;
import plugins.perrine.easyclemv0.model.Point;

public class RoiFactory {

    public ROI getFrom(Point point) {
        ROI roi;
        switch (point.getDimension()) {
            case 2: roi = new ROI2DPoint();
                          break;
            case 3: roi = new ROI3DPoint();
                          break;
            default: throw new RuntimeException("Unsupported dimension : " + point.getDimension());
        }
        Point5D position = roi.getPosition5D();
        for(int i = 0; i < point.getDimension(); i++) {
            if(i == 0) {
                position.setX(point.get(i));
            }
            if(i == 1) {
                position.setY(point.get(i));
            }
            if(i == 2) {
                position.setZ(point.get(i));
            }
        }
        roi.setPosition5D(position);
        return roi;
    }
}
