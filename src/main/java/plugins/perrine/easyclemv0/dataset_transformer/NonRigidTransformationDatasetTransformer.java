package plugins.perrine.easyclemv0.dataset_transformer;

import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.SplineTransformation;
import plugins.perrine.easyclemv0.registration.NonRigidTransformationComputer;

public class NonRigidTransformationDatasetTransformer {

    private NonRigidTransformationComputer nonRigidTransformationComputer = new NonRigidTransformationComputer();

    public Dataset apply(FiducialSet fiducialSet, Dataset dataset) {
        SplineTransformation splineTransformation = nonRigidTransformationComputer.compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
        return splineTransformation.apply(dataset);
    }
}
