package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.TransformationSchema;
import plugins.perrine.easyclemv0.model.Workspace;

public class TransformationSchemaFactory {

    private FiducialSetFactory fiducialSetFactory = new FiducialSetFactory();
    private SequenceSizeFactory sequenceSizeFactory = new SequenceSizeFactory();

    public TransformationSchema getFrom(Workspace workspace) {
        FiducialSet fiducialSet = fiducialSetFactory.getFrom(workspace);
        return new TransformationSchema(
            fiducialSet,
            workspace.getTransformationConfiguration().getTransformationType(),
            sequenceSizeFactory.getFrom(workspace.getSourceSequence()),
            sequenceSizeFactory.getFrom(workspace.getTargetSequence())
        );
    }
}
