package plugins.perrine.easyclemv0.model;

import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.VtkPointsFactory;
import vtk.*;

public class SplineTransformation {

    private vtkThinPlateSplineTransform splineTransform;
    private VtkPointsFactory vtkPointsFactory = new VtkPointsFactory();
    private DatasetFactory datasetFactory = new DatasetFactory();

    public SplineTransformation(vtkThinPlateSplineTransform splineTransform) {
        this.splineTransform = splineTransform;
    }

    public vtkThinPlateSplineTransform getSplineTransform() {
        return splineTransform;
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
