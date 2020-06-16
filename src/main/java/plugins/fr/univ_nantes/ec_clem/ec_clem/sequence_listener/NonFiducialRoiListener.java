package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence_listener;

import icy.main.Icy;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.RoiFactory;
import plugins.kernel.roi.roi2d.plugin.ROI2DPointPlugin;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import javax.inject.Inject;
import java.util.List;

public class NonFiducialRoiListener implements SequenceListener {

    private static PointType type = PointType.NOT_FIDUCIAL;
    private RoiFactory roiFactory;

    @Inject
    public NonFiducialRoiListener(RoiFactory roiFactory) {
        this.roiFactory = roiFactory;
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
