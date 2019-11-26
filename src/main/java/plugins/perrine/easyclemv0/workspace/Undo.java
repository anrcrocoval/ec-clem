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
package plugins.perrine.easyclemv0.workspace;

import icy.sequence.SequenceListener;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.progress.ProgressTrackableMasterTask;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.sequence_listener.RoiDuplicator;
import plugins.perrine.easyclemv0.sequence_listener.SequenceListenerUtil;
import javax.inject.Inject;
import java.util.List;

public class Undo extends ProgressTrackableMasterTask implements Runnable {

    private SequenceListenerUtil sequenceListenerUtil;
    private DatasetFactory datasetFactory;
    private RoiUpdater roiUpdater;

    private Workspace workspace;
    private ResetOriginalImage resetOriginalImage;
    private WorkspaceTransformer workspaceTransformer;

    public Undo(Workspace workspace) {
        DaggerUndoComponent.builder().build().inject(this);
        this.workspace = workspace;
        resetOriginalImage = new ResetOriginalImage(workspace);
        workspaceTransformer = new WorkspaceTransformer(workspace);
        super.add(resetOriginalImage);
        super.add(workspaceTransformer);
    }

    @Override
    public void run() {
        List<SequenceListener> targetSequenceListeners = sequenceListenerUtil.removeListeners(workspace.getTargetSequence(), RoiDuplicator.class);
        resetOriginalImage.run();
        Dataset sourceDataset = datasetFactory.getFrom(workspace.getSourceSequence());
        sourceDataset.removePoint(sourceDataset.getN() - 1);
        Dataset targetDataset = datasetFactory.getFrom(workspace.getTargetSequence());
        targetDataset.removePoint(targetDataset.getN() - 1);
        roiUpdater.updateRoi(sourceDataset, workspace.getSourceSequence());
        roiUpdater.updateRoi(targetDataset, workspace.getTargetSequence());
        sequenceListenerUtil.addListeners(workspace.getTargetSequence(), targetSequenceListeners);
        workspaceTransformer.run();
    }

    @Inject
    public void setSequenceListenerUtil(SequenceListenerUtil sequenceListenerUtil) {
        this.sequenceListenerUtil = sequenceListenerUtil;
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
