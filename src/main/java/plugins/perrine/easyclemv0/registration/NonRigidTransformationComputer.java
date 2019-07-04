package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.transformation.SplineTransformation;

public class NonRigidTransformationComputer {

    private SplineRegistration splineRegistration = new SplineRegistration();

    public SplineTransformation compute(FiducialSet fiducialSet) {
        return splineRegistration.compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }
}
