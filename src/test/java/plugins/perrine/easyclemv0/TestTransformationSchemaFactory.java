package plugins.perrine.easyclemv0;

import plugins.perrine.easyclemv0.model.*;

public class TestTransformationSchemaFactory {

    public TransformationSchema getRigidTransformationSchema(FiducialSet fiducialSet, TransformationType transformationType, SequenceSize sourceSize, SequenceSize targetSize) {
        return new TransformationSchema(
            fiducialSet,
            transformationType,
            sourceSize,
            targetSize
        );
    }
}