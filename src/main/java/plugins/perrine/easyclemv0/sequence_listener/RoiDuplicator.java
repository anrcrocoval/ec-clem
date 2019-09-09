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
package plugins.perrine.easyclemv0.sequence_listener;

import icy.gui.frame.progress.AnnounceFrame;
import icy.main.Icy;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import icy.type.point.Point5D;
import plugins.kernel.roi.roi2d.plugin.ROI2DPointPlugin;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import plugins.perrine.easyclemv0.workspace.WorkspaceState;

import javax.inject.Inject;
import java.util.List;

import static plugins.perrine.easyclemv0.EasyCLEMv0.Colortab;

public class RoiDuplicator implements SequenceListener {

    private Sequence sequence;
    private WorkspaceState workspaceState;
    private SequenceListenerUtil sequenceListenerUtil;

    @Inject
    public RoiDuplicator(SequenceListenerUtil sequenceListenerUtil) {
        this.sequenceListenerUtil = sequenceListenerUtil;
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
        double z = event.getSequence().getFirstViewer().getPositionZ();

        ROI roi = (ROI) event.getSource();
        Point5D pos = roi.getPosition5D();
        pos.setZ(z);
        roi.setPosition5D(pos);

        roi.setColor(Colortab[(event.getSequence().getROICount(ROI.class) - 1) % Colortab.length]);
        roi.setName("Point " + event.getSequence().getROIs().size());
        roi.setStroke(6);

        ROI roisource = roi.getCopy();
        if (sequence == null) {
            new AnnounceFrame("You've closed the source image");
            return;
        }
        int zs = sequence.getFirstViewer().getPositionZ(); // was
        Point5D pos2 = roisource.getPosition5D();
        pos2.setZ(zs);
        roisource.setPosition5D(pos2);
        if ((sequence.getWidth() != event.getSequence().getWidth()) || (sequence.getHeight() != event.getSequence().getHeight())) {
            Point5D position = (Point5D) pos.clone();
            position.setLocation(sequence.getWidth() / 2, sequence.getHeight() / 2,
                sequence.getFirstViewer().getPositionZ(),
                sequence.getFirstViewer().getPositionT(), pos.getC());
            roisource.setPosition5D(position);

        }
        roisource.setColor(roi.getColor());
        roisource.setName(roi.getName());
        roisource.setStroke(roi.getStroke());
        roisource.setFocused(false);
        List<SequenceListener> sequenceListeners = sequenceListenerUtil.removeListeners(sequence, RoiDuplicator.class);
        sequence.addROI(roisource);
        sequenceListenerUtil.addListeners(sequence, sequenceListeners);
        workspaceState.setFlagReadyToMove(true);
        workspaceState.setDone(false);

        Icy.getMainInterface().setSelectedTool(getSelectedTool(roi).getName());
    }

    @Override
    public void sequenceClosed(Sequence sequence) {

    }

    private Class<?> getSelectedTool(ROI roi) {
        switch (roi.getDimension()) {
            case 2 : return ROI2DPointPlugin.class;
            case 3 : return ROI3DPointPlugin.class;
            default: throw new RuntimeException("Unsupported dimension");
        }
    }
}
