package plugins.perrine.easyclemv0.image_transformer;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.SequenceSize;

public interface ImageTransformerInterface {
    void setSourceSequence(Sequence sequence);
    void setTargetSize(SequenceSize targetSize);
    void setTargetSize(Sequence sequence);
    void run(FiducialSet fiducialSet);
}
