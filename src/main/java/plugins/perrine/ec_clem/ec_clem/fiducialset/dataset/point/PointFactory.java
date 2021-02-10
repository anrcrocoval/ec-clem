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
package plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point;

import Jama.Matrix;
import icy.roi.ROI;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import plugins.perrine.ec_clem.ec_clem.roi.RoiProcessor;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceSize;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.roi.RoiProcessor;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceSize;
import vtk.vtkPolyData;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

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

    public Point getFrom(vtkPolyData points) {
        return new Point(points.GetPoint(0));
    }

    public List<Point> getFrom(Dataset dataset) {
        List<Point> list = new LinkedList<>();
        for(int i = 0; i < dataset.getN(); i++) {
            list.add(dataset.getPoint(i));
        }
        return list;
    }

    public Point toPixel(Point point, SequenceSize sequenceSize) {
        Matrix M = point.getMatrix().copy();
        
        for(int d = 0; d < point.getDimension(); d++) {
            if(d == 0) {
                M.set(d, 0, M.get(d, 0) / sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer());
            }
            if(d == 1) {
                M.set(d, 0, M.get(d, 0) / sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer());
            }
            if(d == 2) {
                M.set(d, 0, M.get(d, 0) / sequenceSize.get(DimensionId.Z).getPixelSizeInMicrometer());
            }
        }
       
        return new Point(M);
    }
    public Point toPixel3D(Point point, SequenceSize sequenceSize) {
       if (point.getDimension()==3) {
    	   return toPixel(point,sequenceSize);
       }
       else {
    	   Matrix M = new Matrix(3,1);
    	   M.set(2, 0, 0.5);
    	   for(int d = 0; d < point.getDimension(); d++) {
               if(d == 0) {
                   M.set(d, 0, point.get(d) / sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer());
               }
               if(d == 1) {
                   M.set(d, 0, point.get(d) / sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer());
               }
              
           }
    	   
    	   return new Point(M);
       }

    }
}
