package plugins.perrine.easyclemv0.model;

import javax.sound.midi.Sequence;

public class Transformation {
    private FiducialSet fiducialSet;
    private TransformationType transformationType;
    private SequenceSize sourceSize;
    private SequenceSize targetSize;

    public Transformation(FiducialSet fiducialSet, TransformationType transformationType, SequenceSize sourceSize, SequenceSize targetSize) {
        this.fiducialSet = fiducialSet;
        this.transformationType = transformationType;
        this.sourceSize = sourceSize;
        this.targetSize = targetSize;
    }

    public FiducialSet getFiducialSet() {
        return fiducialSet;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public SequenceSize getSourceSize() {
        return sourceSize;
    }

    public SequenceSize getTargetSize() {
        return targetSize;
    }

    public Transformation inverse() {
        return new Transformation(
            new FiducialSet(fiducialSet.getTargetDataset(), fiducialSet.getSourceDataset()),
            transformationType,
            targetSize,
            sourceSize
        );
    }
}
