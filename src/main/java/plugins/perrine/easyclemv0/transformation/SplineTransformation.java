package plugins.perrine.easyclemv0.transformation;

import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.sequence.VtkPointsFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import vtk.*;

import javax.inject.Inject;

public class SplineTransformation implements Transformation {

    private vtkThinPlateSplineTransform splineTransform;
    private VtkPointsFactory vtkPointsFactory;
    private DatasetFactory datasetFactory;

    @Inject
    public SplineTransformation(VtkPointsFactory vtkPointsFactory, DatasetFactory datasetFactory) {
        this.vtkPointsFactory = vtkPointsFactory;
        this.datasetFactory = datasetFactory;
    }

    public vtkThinPlateSplineTransform getSplineTransform() {
        return splineTransform;
    }

    public SplineTransformation setSplineTransform(vtkThinPlateSplineTransform splineTransform) {
        this.splineTransform = splineTransform;
        return this;
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
