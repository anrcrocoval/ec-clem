package plugins.perrine.easyclemv0.transformation.schema;

import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;

public class TransformationSchema {
    private FiducialSet fiducialSet;
    private TransformationType transformationType;
    private SequenceSize sourceSize;
    private SequenceSize targetSize;

    public TransformationSchema(FiducialSet fiducialSet, TransformationType transformationType, SequenceSize sourceSize, SequenceSize targetSize) {
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

    public TransformationSchema inverse() {
        return new TransformationSchema(
            new FiducialSet(fiducialSet.getTargetDataset(), fiducialSet.getSourceDataset()),
            transformationType,
            targetSize,
            sourceSize
        );
    }
}
