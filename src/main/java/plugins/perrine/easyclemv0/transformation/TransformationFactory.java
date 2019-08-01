package plugins.perrine.easyclemv0.transformation;

import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.test.registration.AffineTransformationComputer;
import plugins.perrine.easyclemv0.test.registration.NonRigidTransformationComputer;
import plugins.perrine.easyclemv0.test.registration.RigidTransformationComputer;
import plugins.perrine.easyclemv0.test.registration.SimilarityTransformationComputer;

import javax.inject.Inject;

public class TransformationFactory {

    private RigidTransformationComputer rigidTransformationComputer;
    private SimilarityTransformationComputer similarityTransformationComputer;
    private NonRigidTransformationComputer nonRigidTransformationComputer;
    private AffineTransformationComputer affineTransformationComputer;

    @Inject
    public TransformationFactory(RigidTransformationComputer rigidTransformationComputer, SimilarityTransformationComputer similarityTransformationComputer, NonRigidTransformationComputer nonRigidTransformationComputer, AffineTransformationComputer affineTransformationComputer) {
        this.rigidTransformationComputer = rigidTransformationComputer;
        this.similarityTransformationComputer = similarityTransformationComputer;
        this.nonRigidTransformationComputer = nonRigidTransformationComputer;
        this.affineTransformationComputer = affineTransformationComputer;
    }

    public Transformation getFrom(TransformationSchema transformationSchema) {
        switch (transformationSchema.getTransformationType()) {
            case RIGID: return rigidTransformationComputer.compute(transformationSchema.getFiducialSet());
            case SIMILARITY: return similarityTransformationComputer.compute(transformationSchema.getFiducialSet());
            case AFFINE: return affineTransformationComputer.compute(transformationSchema.getFiducialSet());
            case SPLINE: return  nonRigidTransformationComputer.compute(transformationSchema.getFiducialSet());
            default : throw new RuntimeException("Case not implemented");
        }
    }
}
