package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.SplineTransformation;

public class NonRigidTransformationComputer {

    private SplineRegistration splineRegistration = new SplineRegistration();

    public SplineTransformation compute(Dataset sourceDataset, Dataset targetDataset) {
        return splineRegistration.compute(sourceDataset, targetDataset);
    }
}
