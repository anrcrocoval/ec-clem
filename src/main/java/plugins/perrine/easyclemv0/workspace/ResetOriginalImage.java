package plugins.perrine.easyclemv0.workspace;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.progress.ProgressReport;
import plugins.perrine.easyclemv0.progress.ProgressTrackable;
import plugins.perrine.easyclemv0.progress.ChildProgressReport;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.progress.ProgressManager;
import javax.inject.Inject;

public class ResetOriginalImage implements Runnable, ProgressTrackable {

    private DatasetFactory datasetFactory;
    private RoiUpdater roiUpdater;

    private Workspace workspace;
    private ChildProgressReport progressReport;

    public ResetOriginalImage(Workspace workspace) {
        DaggerResetOriginalImageComponent.builder().build().inject(this);
        this.workspace = workspace;
        progressReport = new ChildProgressReport(1);
    }

    @Override
    public void visit(ProgressManager progressManager) {
        progressManager.add(this);
    }

    @Override
    public ProgressReport getProgress() {
        return progressReport;
    }

    @Override
    public void run() {
        if(workspace.getTransformationSchema() != null) {
            Dataset reversed = datasetFactory.getFrom(
                datasetFactory.getFrom(workspace.getSourceSequence()),
                workspace.getTransformationSchema().inverse()
            );
            restoreBackup(workspace.getSourceSequence(), workspace.getSourceBackup());
            roiUpdater.updateRoi(reversed, workspace.getSourceSequence());
            workspace.setTransformationSchema(null);
            workspace.getSourceSequence().setName(workspace.getOriginalNameofSource());
        }
        progressReport.incrementCompleted();
    }

    private void restoreBackup(Sequence sequence, Sequence backup) {
        sequence.setAutoUpdateChannelBounds(false);
        sequence.beginUpdate();
        sequence.removeAllImages();
        try {
            for (int t = 0; t < backup.getSizeT(); t++) {
                for (int z = 0; z < backup.getSizeZ(); z++) {
                    sequence.setImage(t, z, backup.getImage(t, z));
                }
            }
            sequence.setPixelSizeX(backup.getPixelSizeX());
            sequence.setPixelSizeY(backup.getPixelSizeY());
            sequence.setPixelSizeZ(backup.getPixelSizeZ());
        } finally {
            sequence.endUpdate();
        }
    }

    @Inject
    public void setDatasetFactory(DatasetFactory datasetFactory) {
        this.datasetFactory = datasetFactory;
    }

    @Inject
    public void setRoiUpdater(RoiUpdater roiUpdater) {
        this.roiUpdater = roiUpdater;
    }
}
