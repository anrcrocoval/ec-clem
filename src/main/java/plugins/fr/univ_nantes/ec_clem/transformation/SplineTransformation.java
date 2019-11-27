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
package plugins.fr.univ_nantes.ec_clem.transformation;

import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.DatasetFactory;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.PointFactory;
import plugins.fr.univ_nantes.ec_clem.sequence.VtkPointsFactory;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import vtk.*;

import javax.inject.Inject;

public class SplineTransformation implements Transformation {

    private vtkThinPlateSplineTransform splineTransform;
    private VtkPointsFactory vtkPointsFactory;
    private DatasetFactory datasetFactory;
    private PointFactory pointFactory;

    @Inject
    public SplineTransformation(VtkPointsFactory vtkPointsFactory, DatasetFactory datasetFactory, PointFactory pointFactory) {
        this.vtkPointsFactory = vtkPointsFactory;
        this.datasetFactory = datasetFactory;
        this.pointFactory = pointFactory;
    }

    public vtkThinPlateSplineTransform getSplineTransform() {
        return splineTransform;
    }

    public SplineTransformation setSplineTransform(vtkThinPlateSplineTransform splineTransform) {
        this.splineTransform = splineTransform;
        return this;
    }

    public Point apply(Point point) {
        vtkPolyData vtkPointsResult = apply(vtkPointsFactory.getFrom(point), splineTransform);
        return pointFactory.getFrom(vtkPointsResult);
    }

    public Dataset apply(Dataset dataset) {
        vtkPolyData vtkPointsResult = apply(vtkPointsFactory.getFrom(dataset), splineTransform);
        return datasetFactory.getFrom(vtkPointsResult);
    }

    private vtkPolyData apply(vtkPoints sourcePoints, vtkThinPlateSplineTransform transform) {
        vtkPolyData mypoints = new vtkPolyData();
        mypoints.SetPoints(sourcePoints);

        vtkVertexGlyphFilter vertexfilter = new vtkVertexGlyphFilter();
        vertexfilter.SetInputData(mypoints);
        vertexfilter.Update();

        vtkPolyData sourcepolydata = new vtkPolyData();
        sourcepolydata.ShallowCopy(vertexfilter.GetOutput());

        vtkTransformPolyDataFilter tr = new  vtkTransformPolyDataFilter();
        tr.SetInputData(sourcepolydata);
        tr.SetTransform(transform);
        tr.Update();

        return tr.GetOutput();
    }
}
