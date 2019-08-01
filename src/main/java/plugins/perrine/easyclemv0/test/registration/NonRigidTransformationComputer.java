package plugins.perrine.easyclemv0.test.registration;

import plugins.perrine.easyclemv0.sequence.VtkPointsFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.transformation.DaggerSplineTransformationComponent;
import plugins.perrine.easyclemv0.transformation.SplineTransformation;
import plugins.perrine.easyclemv0.transformation.SplineTransformationComponent;
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
