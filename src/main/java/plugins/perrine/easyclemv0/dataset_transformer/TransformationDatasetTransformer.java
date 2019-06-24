package plugins.perrine.easyclemv0.dataset_transformer;

import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Transformation;

public class TransformationDatasetTransformer {

    private RigidTransformationDatasetTransformer rigidTransformationDatasetTransformer = new RigidTransformationDatasetTransformer();
    private NonRigidTransformationDatasetTransformer nonRigidTransformationDatasetTransformer = new NonRigidTransformationDatasetTransformer();

    public Dataset apply(Transformation transformation, Dataset dataset) {
        switch (transformation.getTransformationType()) {
            case RIGID: return rigidTransformationDatasetTransformer.apply(transformation.getFiducialSet(), dataset);
            case NON_RIGID: return nonRigidTransformationDatasetTransformer.apply(transformation.getFiducialSet(), dataset);
            default : throw new RuntimeException("Case not implemented");
        }
    }
}
