package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence_listener;

import icy.sequence.Sequence;
import icy.sequence.SequenceListener;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import javax.inject.Inject;
import java.util.List;

public class RoiListenerManager {

    private Sequence sourceSequence;
    private Sequence targetSequence;

    private SequenceListenerUtil sequenceListenerUtil;
    private FiducialRoiListener sourceSequenceFiducialRoiListener;
    private FiducialRoiListener targetSequenceFiducialRoiListener;
    private NonFiducialRoiListener targetSequenceNonFiducialRoiListener;

    @Inject
    public RoiListenerManager(SequenceListenerUtil sequenceListenerUtil) {
        this.sequenceListenerUtil = sequenceListenerUtil;
    }

    @Inject
    public void setSourceSequenceFiducialRoiListener(FiducialRoiListener sourceSequenceFiducialRoiListener) {
        this.sourceSequenceFiducialRoiListener = sourceSequenceFiducialRoiListener;
    }

    @Inject
    public void setTargetSequenceFiducialRoiListener(FiducialRoiListener targetSequenceFiducialRoiListener) {
        this.targetSequenceFiducialRoiListener = targetSequenceFiducialRoiListener;
    }

    @Inject
    public void setTargetSequenceNonFiducialRoiListener(NonFiducialRoiListener targetSequenceNonFiducialRoiListener) {
        this.targetSequenceNonFiducialRoiListener = targetSequenceNonFiducialRoiListener;
    }

    public void set(PointType pointType) {
        switch(pointType) {
            case FIDUCIAL:
                clear();
                sourceSequence.addListener(sourceSequenceFiducialRoiListener);
                targetSequence.addListener(targetSequenceFiducialRoiListener);
                break;
            case NOT_FIDUCIAL:
                clear();
                sourceSequence.addListener(targetSequenceNonFiducialRoiListener);
                break;
            default: throw new RuntimeException(String.format("Point type %s not implemented", pointType));
        }
    }

    public void clear() {
        sequenceListenerUtil.removeListeners(sourceSequence, FiducialRoiListener.class);
        sequenceListenerUtil.removeListeners(targetSequence, FiducialRoiListener.class);
        sequenceListenerUtil.removeListeners(sourceSequence, NonFiducialRoiListener.class);
        sequenceListenerUtil.removeListeners(targetSequence, NonFiducialRoiListener.class);
    }

    public List<SequenceListener> removeAll(Sequence sequence) {
        List<SequenceListener> removed = sequenceListenerUtil.removeListeners(sequence, FiducialRoiListener.class);
        removed.addAll(sequenceListenerUtil.removeListeners(sequence, NonFiducialRoiListener.class));
        return removed;
    }

    public void setSequences(Sequence sourceSequence, Sequence targetSequence) {
        this.sourceSequence = sourceSequence;
        this.targetSequence = targetSequence;
        sourceSequenceFiducialRoiListener.setSequence(targetSequence);
        targetSequenceFiducialRoiListener.setSequence(sourceSequence);
    }
}
