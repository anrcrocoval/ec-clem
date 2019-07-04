package plugins.perrine.easyclemv0.image_transformer;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.model.transformation.Transformation;

public interface ImageTransformer {
    void setSourceSequence(Sequence sequence);
    void setTargetSize(SequenceSize targetSize);
    void run(Transformation transformation);
}
