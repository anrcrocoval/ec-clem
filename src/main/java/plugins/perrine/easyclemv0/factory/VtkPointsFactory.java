package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.Dataset;
import vtk.vtkPoints;

public class VtkPointsFactory {

    public vtkPoints getFrom(Dataset dataset) {
        vtkPoints points = new vtkPoints();
        points.SetNumberOfPoints(dataset.getN());
        for (int i = 0; i < dataset.getN(); i++) {
            points.SetPoint(i,dataset.getPoint(i).getmatrix().transpose().getArray()[0]);
        }
        return points;
    }
}
