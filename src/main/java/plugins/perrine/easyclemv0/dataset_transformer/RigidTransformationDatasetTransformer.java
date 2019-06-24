package plugins.perrine.easyclemv0.dataset_transformer;

import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Similarity;
import plugins.perrine.easyclemv0.registration.RigidTransformationComputer;

public class RigidTransformationDatasetTransformer {

    private RigidTransformationComputer rigidTransformationComputer = new RigidTransformationComputer();

    public Dataset apply(FiducialSet fiducialSet, Dataset dataset) {
        Similarity similarity = rigidTransformationComputer.compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
        return similarity.apply(dataset);
    }
}
