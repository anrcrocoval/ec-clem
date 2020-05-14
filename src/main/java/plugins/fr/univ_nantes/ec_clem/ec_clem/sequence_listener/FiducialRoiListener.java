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
package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence_listener;

import icy.canvas.IcyCanvas2D;
import icy.main.Icy;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import icy.type.point.Point5D;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.RoiFactory;
import plugins.kernel.roi.roi2d.plugin.ROI2DPointPlugin;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import javax.inject.Inject;
import java.awt.geom.Point2D;
import java.util.List;

public class FiducialRoiListener implements SequenceListener {

    private static PointType type = PointType.FIDUCIAL;
    private Sequence sequence;
    private SequenceListenerUtil sequenceListenerUtil;
    private RoiFactory roiFactory;

    @Inject
    public FiducialRoiListener(SequenceListenerUtil sequenceListenerUtil, RoiFactory roiFactory) {
        this.sequenceListenerUtil = sequenceListenerUtil;
        this.roiFactory = roiFactory;
    }

    public FiducialRoiListener setSequence(Sequence sequence) {
        this.sequence = sequence;
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

        ROI roi = roiFactory.getRoiFrom(
            (ROI) event.getSource(),
            roiFactory.getFrom(event.getSequence(), type).size() + 1,
            type
        );

        ROI roiCopy = roi.getCopy();
        Point2D.Double imagePosition = ((IcyCanvas2D) sequence.getFirstViewer().getCanvas()).canvasToImage(
        sequence.getFirstViewer().getCanvas().getCanvasSizeX() / 2,
        sequence.getFirstViewer().getCanvas().getCanvasSizeY() / 2
        );
        Point5D position = (Point5D) roi.getPosition5D().clone();
        position.setLocation(
            imagePosition.getX(),
            imagePosition.getY(),
            sequence.getFirstViewer().getPositionZ(),
            sequence.getFirstViewer().getPositionT(),
            sequence.getFirstViewer().getPositionC()
        );
        roiCopy.setPosition5D(position);

        List<SequenceListener> sequenceListeners = sequenceListenerUtil.removeListeners(sequence, FiducialRoiListener.class);
        sequence.addROI(roiCopy);
        sequenceListenerUtil.addListeners(sequence, sequenceListeners);
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
