package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.factory.vtk.VtkPointsFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.transformation.DaggerSplineTransformationComponent;
import plugins.perrine.easyclemv0.model.transformation.SplineTransformation;
import plugins.perrine.easyclemv0.model.transformation.SplineTransformationComponent;
import vtk.vtkThinPlateSplineTransform;

import javax.inject.Inject;

public class NonRigidTransformationComputer implements TransformationComputer {

    private SplineTransformationComponent splineTransformationComponent;
    private VtkPointsFactory vtkPointsFactory;

    @Inject
    public NonRigidTransformationComputer(VtkPointsFactory vtkPointsFactory) {
        this.vtkPointsFactory = vtkPointsFactory;
        splineTransformationComponent = DaggerSplineTransformationComponent.create();
    }

    public SplineTransformation compute(FiducialSet fiducialSet) {
        return compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }

    private SplineTransformation compute(Dataset sourceDataset, Dataset targetDataset) {
        vtkThinPlateSplineTransform vtkSplineTransformation = new vtkThinPlateSplineTransform();
        vtkSplineTransformation.SetSourceLandmarks(vtkPointsFactory.getFrom(sourceDataset));
        vtkSplineTransformation.SetTargetLandmarks(vtkPointsFactory.getFrom(targetDataset));
        vtkSplineTransformation.SetBasisToR2LogR();
        return splineTransformationComponent.getSplineTransformation().setSplineTransform(vtkSplineTransformation);
    }
}
