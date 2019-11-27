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
package plugins.fr.univ_nantes.ec_clem.sequence_listener;

import icy.gui.frame.progress.AnnounceFrame;
import icy.main.Icy;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import icy.type.point.Point5D;
import plugins.fr.univ_nantes.ec_clem.roi.RoiFactory;
import plugins.kernel.roi.roi2d.plugin.ROI2DPointPlugin;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import plugins.fr.univ_nantes.ec_clem.workspace.WorkspaceState;

import javax.inject.Inject;
import java.util.List;

import static plugins.fr.univ_nantes.ec_clem.EasyCLEMv0.Colortab;

public class RoiDuplicator implements SequenceListener {

    private Sequence sequence;
    private WorkspaceState workspaceState;
    private SequenceListenerUtil sequenceListenerUtil;
    private RoiFactory roiFactory;

    @Inject
    public RoiDuplicator(SequenceListenerUtil sequenceListenerUtil, RoiFactory roiFactory) {
        this.sequenceListenerUtil = sequenceListenerUtil;
        this.roiFactory = roiFactory;
    }

    public RoiDuplicator setSequence(Sequence sequence) {
        this.sequence = sequence;
        return this;
    }

    public RoiDuplicator setWorkspaceState(WorkspaceState workspaceState) {
        this.workspaceState = workspaceState;
        return this;
    }

    @Override
    public void sequenceChanged(SequenceEvent event) {
        if (
            event.getSourceType() != SequenceEvent.SequenceEventSourceType.SEQUENCE_ROI ||
                event.getType() != SequenceEvent.SequenceEventType.ADDED
        ) {
            return;
        }

        workspaceState.setFlagReadyToMove(false);

        ROI roi = roiFactory.getFiducialRoiFrom(
            (ROI) event.getSource(),
            Colortab[(event.getSequence().getROICount(ROI.class) - 1) % Colortab.length],
            event.getSequence().getROIs().size()
        );

        ROI roiCopy = roi.getCopy();
        if ((sequence.getWidth() != event.getSequence().getWidth()) || (sequence.getHeight() != event.getSequence().getHeight())) {
            Point5D position = (Point5D) roi.getPosition5D().clone();
            position.setLocation(
                sequence.getWidth() / 2d,
                sequence.getHeight() / 2d,
                sequence.getFirstViewer().getPositionZ(),
                sequence.getFirstViewer().getPositionT(),
                sequence.getFirstViewer().getPositionC()
            );
            roiCopy.setPosition5D(position);
        }

        List<SequenceListener> sequenceListeners = sequenceListenerUtil.removeListeners(sequence, RoiDuplicator.class);
        sequence.addROI(roiCopy);
        sequenceListenerUtil.addListeners(sequence, sequenceListeners);
        workspaceState.setFlagReadyToMove(true);
        workspaceState.setDone(false);

        Icy.getMainInterface().setSelectedTool(getSelectedTool(roi).getName());
    }

    @Override
    public void sequenceClosed(Sequence sequence) {}

    private Class<?> getSelectedTool(ROI roi) {
        switch (roi.getDimension()) {
            case 2 : return ROI2DPointPlugin.class;
            case 3 : return ROI3DPointPlugin.class;
            default: throw new RuntimeException("Unsupported dimension");
        }
    }
}
