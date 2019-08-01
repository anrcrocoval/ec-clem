package plugins.perrine.easyclemv0.test.transformation.schema;

import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.schema.TransformationType;

import javax.inject.Inject;

public class TestTransformationSchemaFactory {

    @Inject
    public TestTransformationSchemaFactory() {
    }

    public TransformationSchema getRigidTransformationSchema(FiducialSet fiducialSet, TransformationType transformationType, SequenceSize sourceSize, SequenceSize targetSize) {
        return new TransformationSchema(
            fiducialSet,
            transformationType,
            sourceSize,
            targetSize
        );
    }
}