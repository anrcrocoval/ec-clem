package plugins.perrine.easyclemv0.transformation.schema;

import plugins.perrine.easyclemv0.sequence.SequenceSizeFactory;
import plugins.perrine.easyclemv0.fiducialset.FiducialSetFactory;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.workspace.Workspace;

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
