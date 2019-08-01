/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.easyclemv0.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import icy.sequence.SequenceListener;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.sequence_listener.RoiDuplicator;
import plugins.perrine.easyclemv0.sequence_listener.SequenceListenerUtil;
import plugins.perrine.easyclemv0.workspace.Workspace;
import plugins.perrine.easyclemv0.workspace.WorkspaceTransformer;
import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

public class UndoButton extends JButton {

	private static final long serialVersionUID = 1L;
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
