/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import plugins.kernel.roi.roi3d.ROI3DPoint;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoiProcessor {

    private Pattern pattern = Pattern.compile(".*?(\\d+)$");

    @Inject
    public RoiProcessor() {}

    public double[][] getPointsFromRoi(ArrayList<ROI> roiList) {
        if(roiList.size() == 0) {
            return null;
        }

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

    private void sort(ArrayList<ROI> roiList) {
        roiList.sort(Comparator.comparing((roi) -> {
            Matcher matcher = pattern.matcher(roi.getName());
            matcher.matches();
            String group = matcher.group(1);
            return Integer.parseInt(group);
        }));
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
