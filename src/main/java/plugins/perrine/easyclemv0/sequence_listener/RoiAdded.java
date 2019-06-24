package plugins.perrine.easyclemv0.sequence_listener;

import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceEvent;
import icy.sequence.SequenceListener;
import icy.type.point.Point5D;
import plugins.perrine.easyclemv0.model.WorkspaceState;

import static plugins.perrine.easyclemv0.EasyCLEMv0.Colortab;

public class RoiAdded implements SequenceListener {

    private Sequence sequence;
    private WorkspaceState workspaceState;

    public RoiAdded(Sequence sequence, WorkspaceState workspaceState) {
        this.sequence = sequence;
        this.workspaceState = workspaceState;
    }

    @Override
    public void sequenceChanged(SequenceEvent event) {
        if (
            event.getSourceType() != SequenceEvent.SequenceEventSourceType.SEQUENCE_ROI ||
                event.getType() != SequenceEvent.SequenceEventType.ADDED
        ) {
            return;
        }

        if (workspaceState.isStopFlag()) {
            return;
        }

        workspaceState.setFlagReadyToMove(false);
        double z = event.getSequence().getFirstViewer().getPositionZ();

        ROI roi = (ROI) event.getSource();
        Point5D pos = roi.getPosition5D();
        pos.setZ(z);
        roi.setPosition5D(pos);

        int colornb = (int) Math.round(Math.random() * (Colortab.length));
        if (colornb > 8)
            colornb = 8;
        System.out.println("Selected color" + colornb);
        roi.setColor(Colortab[colornb]);
        roi.setName("Point " + event.getSequence().getROIs().size());

        ROI roisource = roi.getCopy();
        if (sequence == null) {
            new AnnounceFrame("You've closed the source image");
            return;
        }
        int zs = sequence.getFirstViewer().getPositionZ(); // was
        Point5D pos2 = roisource.getPosition5D();
        pos2.setZ(zs);
        roisource.setPosition5D(pos2);
        if ((sequence.getWidth() != event.getSequence().getWidth())
            || (sequence.getHeight() != event.getSequence().getHeight())) {
            Point5D position = (Point5D) pos.clone();
            position.setLocation(sequence.getWidth() / 2, sequence.getHeight() / 2,
                sequence.getFirstViewer().getPositionZ(),
                sequence.getFirstViewer().getPositionT(), pos.getC());
            roisource.setPosition5D(position);

        }
        System.out.println("Adding Roi Landmark " + event.getSequence().getROIs().size() + " on source");
        roisource.setName("Point " + event.getSequence().getROIs().size());
        roisource.setStroke(9);
        roisource.setFocused(false);
//        sequence.removeListener(this);
        sequence.addROI(roisource);
        workspaceState.setFlagReadyToMove(true);
        workspaceState.setDone(false);
    }

    @Override
    public void sequenceClosed(Sequence sequence) {

    }
}
