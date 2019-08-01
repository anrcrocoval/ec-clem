package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.TransformationSchema;
import plugins.perrine.easyclemv0.model.Workspace;

import javax.inject.Inject;

public class TransformationSchemaFactory {

    private FiducialSetFactory fiducialSetFactory;
    private SequenceSizeFactory sequenceSizeFactory;

    @Inject
    public TransformationSchemaFactory(FiducialSetFactory fiducialSetFactory, SequenceSizeFactory sequenceSizeFactory) {
        this.fiducialSetFactory = fiducialSetFactory;
        this.sequenceSizeFactory = sequenceSizeFactory;
    }

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
