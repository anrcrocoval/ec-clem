package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Transformation;
import plugins.perrine.easyclemv0.model.Workspace;

public class TransformationFactory {

    private FiducialSetFactory fiducialSetFactory = new FiducialSetFactory();
    private SequenceSizeFactory sequenceSizeFactory = new SequenceSizeFactory();

    public Transformation getFrom(Workspace workspace) {
        FiducialSet fiducialSet = fiducialSetFactory.getFrom(workspace);
        return new Transformation(
            fiducialSet,
            workspace.getTransformationConfiguration().getTransformationType(),
            sequenceSizeFactory.getFrom(workspace.getTargetSequence())
        );
    }
}
