package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Workspace;

import javax.inject.Inject;

public class FiducialSetFactory {

    private DatasetFactory datasetFactory;

    @Inject
    public FiducialSetFactory(DatasetFactory datasetFactory) {
        this.datasetFactory = datasetFactory;
    }

    public FiducialSet getFrom(Workspace workspace) {
        return new FiducialSet(
            datasetFactory.getFrom(workspace.getSourceSequence()),
            datasetFactory.getFrom(workspace.getTargetSequence())
        );
    }
}
