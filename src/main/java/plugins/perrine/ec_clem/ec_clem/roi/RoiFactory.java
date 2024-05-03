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
package plugins.perrine.ec_clem.ec_clem.roi;

import Jama.Matrix;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import icy.type.point.Point3D;
import plugins.adufour.roi.mesh.polygon.ROI3DPolygonalMesh;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.Ellipse;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import icy.roi.BooleanMask2D;
import icy.roi.ROI;
import icy.type.point.Point5D;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.PointFactory;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceSize;
import plugins.perrine.ec_clem.ec_clem.sequence.VtkAbstractTransformFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.kernel.roi.roi2d.ROI2DEllipse;
import plugins.kernel.roi.roi2d.ROI2DLine;
import plugins.kernel.roi.roi2d.ROI2DPoint;
import plugins.kernel.roi.roi3d.ROI3DLine;
import plugins.kernel.roi.roi3d.ROI3DPoint;
import vtk.vtkAbstractTransform;
import vtk.vtkParametricEllipsoid;
import vtk.vtkParametricFunctionSource;
import vtk.vtkTransformPolyDataFilter;
import javax.inject.Inject;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.stream.Collectors;

public class RoiFactory {

    private static String POINT_TYPE_PROPERTY = "point_type";
    private VtkAbstractTransformFactory vtkAbstractTransformFactory;
    private PointFactory pointFactory;
    private ColorPicker colorPicker;

    @Inject
    public RoiFactory(VtkAbstractTransformFactory vtkAbstractTransformFactory, PointFactory pointFactory, ColorPicker colorPicker) {
        this.vtkAbstractTransformFactory = vtkAbstractTransformFactory;
        this.pointFactory = pointFactory;
        this.colorPicker = colorPicker;
    }

    public ROI getFrom(Rectangle2D ellipseBound) {
        ROI roi =  new ROI2DEllipse(ellipseBound);
        roi.setColor(PointType.PREDICTED_ERROR.getColor());
        roi.setProperty(POINT_TYPE_PROPERTY, PointType.PREDICTED_ERROR.name());
        return roi;
    }

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

    public ROI getRoiFrom(ROI roi , int id, PointType pointType) {
        roi.setColor(colorPicker.get());
        roi.setName(String.format("%s_%d", pointType, id));
        roi.setShowName(true);
        roi.setStroke(6);
        roi.setProperty(POINT_TYPE_PROPERTY, pointType.name());
        return roi;
    }

    public List<ROI> getFrom(Sequence sequence, PointType pointType) {
        return sequence.getROIs().stream().filter(roi -> pointType.name().equals(roi.getProperty(POINT_TYPE_PROPERTY))).collect(Collectors.toList());
    }
    
    public List<ROI> getFrom(Sequence sequence) {
        return sequence.getROIs();
    }

    public ROI getFrom(Ellipse ellipse, SequenceSize sequenceSize) {
        double[] eigenValues = ellipse.getEigenValues();
        int dimension=3;
        if (eigenValues.length<3) {
        	double[] corrected_eigenvalues=new double[3];
        	corrected_eigenvalues[0]=eigenValues[0];
        	corrected_eigenvalues[1]=eigenValues[1];
        	corrected_eigenvalues[2]=1;
        	eigenValues= corrected_eigenvalues;
        	dimension=2;
        }
        if(
            Math.sqrt(eigenValues[0]) / sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer() > sequenceSize.get(DimensionId.X).getSize() * 2 ||
            Math.sqrt(eigenValues[1]) / sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer() > sequenceSize.get(DimensionId.Y).getSize() * 2 ||
            Math.sqrt(eigenValues[2]) / sequenceSize.get(DimensionId.Z).getPixelSizeInMicrometer() > sequenceSize.get(DimensionId.Z).getSize() * 2
        ) {
            throw new RuntimeException("Error too large");
        }
        vtkParametricEllipsoid ellipsoid = new vtkParametricEllipsoid();
        ellipsoid.SetXRadius(Math.max(Math.sqrt(eigenValues[0]) / sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer(), 1));
        ellipsoid.SetYRadius(Math.max(Math.sqrt(eigenValues[1]) / sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer(), 1));
        ellipsoid.SetZRadius(Math.max(Math.sqrt(eigenValues[2]) / sequenceSize.get(DimensionId.Z).getPixelSizeInMicrometer(), 1));
        vtkParametricFunctionSource parametricFunctionSource = new vtkParametricFunctionSource();
        parametricFunctionSource.SetParametricFunction(ellipsoid);
        parametricFunctionSource.Update();

        vtkTransformPolyDataFilter TransformFilter1 = new vtkTransformPolyDataFilter();
        TransformFilter1.SetInputConnection(parametricFunctionSource.GetOutputPort());
       vtkAbstractTransform transform = vtkAbstractTransformFactory.getFrom(
                new AffineTransformation(ellipse.getEigenVectors().inverse(), new Matrix(dimension, 1, 0)));
       
       TransformFilter1.SetTransform(transform);
        
        TransformFilter1.Update();
            
        ROI3DPolygonalMesh mesh = new ROI3DPolygonalMesh(TransformFilter1.GetOutput());
      
        Point toPixel = pointFactory.toPixel3D(ellipse.getCenter(), sequenceSize);
        Point3D position3D = mesh.getPosition3D();
        position3D.setLocation(
            toPixel.get(0) - mesh.getBounds3D().getSizeX() / 2d,
            toPixel.get(1) - mesh.getBounds3D().getSizeY() / 2d,
            toPixel.get(2) - mesh.getBounds3D().getSizeZ() / 2d
        );
        
        mesh.setPosition3D(position3D);
        mesh.setProperty(POINT_TYPE_PROPERTY, PointType.PREDICTED_ERROR.name());
        mesh.setColor(PointType.PREDICTED_ERROR.getColor());
        
        return mesh;
    }

    public ROI getFrom(Point registered, Point target) {
        assert registered.getDimension() == target.getDimension();
        ROI line;
        switch (registered.getDimension()) {
            case 2 : {
                line = new ROI2DLine(registered.get(0), registered.get(1), target.get(0), target.get(1));
                break;
            }
            case 3 : {
                line = new ROI3DLine(registered.get(0), registered.get(1), registered.get(2), target.get(0), target.get(1), target.get(2));
                break;
            }
            default: throw new RuntimeException("Unsupported dimension : " + registered.getDimension());
        }
        line.setProperty(POINT_TYPE_PROPERTY, PointType.MEASURED_ERROR.name());
        line.setColor(PointType.MEASURED_ERROR.getColor());
        return line;
    }
}
