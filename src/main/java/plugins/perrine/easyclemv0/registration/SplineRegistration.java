package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.factory.VtkPointsFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.SplineTransformation;
import vtk.vtkThinPlateSplineTransform;

public class SplineRegistration {

    private VtkPointsFactory vtkPointsFactory = new VtkPointsFactory();

    public SplineTransformation compute(Dataset sourceDataset, Dataset targetDataset) {
        vtkThinPlateSplineTransform vtkSplineTransformation = new vtkThinPlateSplineTransform();
        vtkSplineTransformation.SetSourceLandmarks(vtkPointsFactory.getFrom(sourceDataset));
        vtkSplineTransformation.SetTargetLandmarks(vtkPointsFactory.getFrom(targetDataset));
        return new SplineTransformation(vtkSplineTransformation);
    }
}
