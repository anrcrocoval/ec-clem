package plugins.perrine.easyclemv0.factory.vtk;

import plugins.perrine.easyclemv0.model.Dataset;
import vtk.vtkPoints;

import javax.inject.Inject;

public class VtkPointsFactory {

    @Inject
    public VtkPointsFactory() {}

    public vtkPoints getFrom(Dataset dataset) {
        vtkPoints points = new vtkPoints();
        points.SetNumberOfPoints(dataset.getN());
        for (int i = 0; i < dataset.getN(); i++) {
            points.SetPoint(i, dataset.getPoint(i).getMatrix().transpose().getArray()[0]);
        }
        return points;
    }
}
