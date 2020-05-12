package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence_listener;

import icy.main.Icy;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.RoiFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.RoiFactory;
import plugins.kernel.roi.roi2d.plugin.ROI2DPointPlugin;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import javax.inject.Inject;

import java.awt.*;
import java.util.List;


public class NonFiducialRoiListener implements SequenceListener {

    private static PointType type = PointType.NOT_FIDUCIAL;

//    private WorkspaceState workspaceState;
    private RoiFactory roiFactory;

    @Inject
    public NonFiducialRoiListener(RoiFactory roiFactory) {
        this.roiFactory = roiFactory;
    }

//    public NonFiducialRoiListener setWorkspaceState(WorkspaceState workspaceState) {
//        this.workspaceState = workspaceState;
//        return this;
//    }

    private int count(List<ROI> roiList) {
        return (int) roiList.stream().filter(roi -> roi.getProperty(String.valueOf(type)) != null).count();
    }

    @Override
    public void sequenceChanged(SequenceEvent event) {
        if (
            event.getSourceType() != SequenceEvent.SequenceEventSourceType.SEQUENCE_ROI ||
                event.getType() != SequenceEvent.SequenceEventType.ADDED
        ) {
            return;
        }

//        workspaceState.setFlagReadyToMove(false);

        ROI roi = roiFactory.getRoiFrom(
            (ROI) event.getSource(),
            roiFactory.getFrom(event.getSequence(), type).size() + 1,
            type
        );

//        workspaceState.setFlagReadyToMove(true);
//        workspaceState.setDone(false);

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
