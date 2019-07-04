package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.transformation.Similarity;

public class RigidTransformationComputer {

    private NDimensionnalSimilarityRegistration nDimensionnalSimilarityRegistration;

    public RigidTransformationComputer() {
        nDimensionnalSimilarityRegistration = new NDimensionnalSimilarityRegistration();
    }

    public Similarity compute(FiducialSet fiducialSet) {
        return nDimensionnalSimilarityRegistration.apply(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }
}
