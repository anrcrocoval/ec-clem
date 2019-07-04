package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.factory.vtk.VtkPointsFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.transformation.SplineTransformation;
import vtk.vtkThinPlateSplineTransform;

public class SplineRegistration {

    private VtkPointsFactory vtkPointsFactory = new VtkPointsFactory();

    public SplineTransformation compute(Dataset sourceDataset, Dataset targetDataset) {
        vtkThinPlateSplineTransform vtkSplineTransformation = new vtkThinPlateSplineTransform();
        vtkSplineTransformation.SetSourceLandmarks(vtkPointsFactory.getFrom(sourceDataset));
        vtkSplineTransformation.SetTargetLandmarks(vtkPointsFactory.getFrom(targetDataset));
        vtkSplineTransformation.SetBasisToR2LogR();
        return new SplineTransformation(vtkSplineTransformation);
    }
}
