package plugins.perrine.easyclemv0.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import icy.sequence.SequenceListener;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.sequence_listener.RoiDuplicator;
import plugins.perrine.easyclemv0.util.SequenceListenerUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

public class UndoButton extends JButton {

    private Workspace workspace;
    private DatasetFactory datasetFactory;
    private RoiUpdater roiUpdater;
    private WorkspaceTransformer workspaceTransformer;
    private SequenceListenerUtil sequenceListenerUtil;

    @Inject
    public UndoButton(DatasetFactory datasetFactory, RoiUpdater roiUpdater, WorkspaceTransformer workspaceTransformer, SequenceListenerUtil sequenceListenerUtil) {
        super("Undo last point");
        this.datasetFactory = datasetFactory;
        this.roiUpdater = roiUpdater;
        this.workspaceTransformer = workspaceTransformer;
        this.sequenceListenerUtil = sequenceListenerUtil;
        setToolTipText("Press this button to cancel the last point edition you have done, it will reverse to the previous state of your image");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        if (workspace.getSourceSequence() == null || workspace.getTargetSequence() == null) {
            MessageDialog.showDialog("Make sure source and target image are openned and selected");
            return;
        }

        List<ROI> listRoisource = workspace.getSourceSequence().getROIs(true);
        if (listRoisource.size() == 0) {
            new AnnounceFrame("Nothing to undo",5);
            return;
        }

        List<SequenceListener> targetSequenceListeners = sequenceListenerUtil.removeListeners(workspace.getTargetSequence(), RoiDuplicator.class);
        workspaceTransformer.resetToOriginalImage(workspace);
        Dataset sourceDataset = datasetFactory.getFrom(workspace.getSourceSequence());
        sourceDataset.removePoint(sourceDataset.getN() - 1);
        Dataset targetDataset = datasetFactory.getFrom(workspace.getTargetSequence());
        targetDataset.removePoint(targetDataset.getN() - 1);
        roiUpdater.updateRoi(sourceDataset, workspace.getSourceSequence());
        roiUpdater.updateRoi(targetDataset, workspace.getTargetSequence());
        sequenceListenerUtil.addListeners(workspace.getTargetSequence(), targetSequenceListeners);
        workspaceTransformer.apply(workspace);
    }
}
