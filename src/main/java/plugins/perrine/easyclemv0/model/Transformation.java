package plugins.perrine.easyclemv0.model;

public class Transformation {
    private FiducialSet fiducialSet;
    private TransformationType transformationType;
    private SequenceSize targetSize;

    public Transformation(FiducialSet fiducialSet, TransformationType transformationType, SequenceSize targetSize) {
        this.fiducialSet = fiducialSet;
        this.transformationType = transformationType;
        this.targetSize = targetSize;
    }

    public FiducialSet getFiducialSet() {
        return fiducialSet;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public SequenceSize getTargetSize() {
        return targetSize;
    }

    public Transformation inverse() {
        return new Transformation(
            new FiducialSet(fiducialSet.getTargetDataset(), fiducialSet.getSourceDataset()),
            transformationType,
            targetSize
        );
    }
}
