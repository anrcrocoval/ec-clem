package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Workspace;

public class FiducialSetFactory {

    private DatasetFactory datasetFactory = new DatasetFactory();

    public FiducialSet getFrom(Workspace workspace) {
        return new FiducialSet(
            datasetFactory.getFrom(workspace.getSourceSequence()),
            datasetFactory.getFrom(workspace.getTargetSequence())
        );
    }
}
