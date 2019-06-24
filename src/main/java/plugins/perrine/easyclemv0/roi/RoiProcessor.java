package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.type.point.Point5D;
import plugins.kernel.roi.descriptor.measure.ROIMassCenterDescriptorsPlugin;
import plugins.kernel.roi.roi3d.ROI3DPoint;

import java.util.ArrayList;

public class RoiProcessor {

    public double[][] getPointsFromRoi(ArrayList<ROI> roiList) {
        int dimension = roiList.get(0).getDimension();
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
        double[] result = new double[roi.getDimension()];
//        Point5D p3D = ROIMassCenterDescriptorsPlugin.computeMassCenter(roi);
//        if (roi.getClassName().equals("plugins.kernel.roi.roi3d.ROI3DPoint"))
//            p3D = roi.getPosition5D();
//        if (Double.isNaN(p3D.getX()))
//            p3D = roi.getPosition5D();
//        result[0] = p3D.getX();
//        result[1] = p3D.getY();
//        result[2] = p3D.getZ();
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
        int longueur = roiList.size();
        ROI tampon;
        boolean permut;
        do {
            permut = false;
            for (int i = 0; i < longueur - 1; i++) {
                if (roiList.get(i).getName().compareTo(roiList.get(i + 1).getName()) > 0) {
                    tampon = roiList.get(i);
                    roiList.set(i, roiList.get(i + 1));
                    roiList.set(i + 1, tampon);
                    permut = true;
                }
            }
        } while (permut);
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
