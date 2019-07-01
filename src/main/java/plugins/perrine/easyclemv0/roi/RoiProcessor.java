package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.type.point.Point5D;
import plugins.kernel.roi.descriptor.measure.ROIMassCenterDescriptorsPlugin;
import plugins.kernel.roi.roi3d.ROI3DPoint;

import java.util.ArrayList;
import java.util.Comparator;

public class RoiProcessor {

    public double[][] getPointsFromRoi(ArrayList<ROI> roiList) {
        if(roiList.size() == 0) {
            throw new RuntimeException("Empty list");
        }

//        int dimension = roiList.get(0).getDimension();
        int dimension = 3;
        sort(roiList);
        double [][] result = new double[roiList.size()][dimension];
        int i = -1;
        for (ROI roi : roiList) {
            i++;
            result[i] = getPointFromRoi(roi);
        }
        return result;
    }

    public double[] getPointFromRoi(ROI roi) {
        double[] result = new double[3];
        for(int i = 0; i < roi.getDimension(); i++) {
            if(i == 0) {
                result[i] = roi.getPosition5D().getX();
            }
            if(i == 1) {
                result[i] = roi.getPosition5D().getY();
            }
            if(i == 2) {
                result[i] = roi.getPosition5D().getZ();
            }
        }
        return result;
    }

    public void sort(ArrayList<ROI> roiList) {
        roiList.sort(Comparator.comparing(ROI::getName));
    }

    public void convert(ArrayList<ROI> roiList) {
        for (int i = 0; i < roiList.size(); i++) {
            ROI roi = roiList.get(i);
            if (!roi.getClassName().equals("plugins.kernel.roi.roi3d.ROI3DPoint")) {
                ROI3DPoint roi3D = new ROI3DPoint(roi.getPosition5D());
                roi3D.setName(roi.getName());
                roi3D.setColor(roi.getColor());
                roi3D.setStroke(roi.getStroke());
                roiList.set(i, roi3D);
            }
        }
    }
}
