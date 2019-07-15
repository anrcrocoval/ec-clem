package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.TransformationSchema;
import plugins.perrine.easyclemv0.model.transformation.Transformation;
import plugins.perrine.easyclemv0.registration.AffineTransformationComputer;
import plugins.perrine.easyclemv0.registration.NonRigidTransformationComputer;
import plugins.perrine.easyclemv0.registration.RigidTransformationComputer;
import plugins.perrine.easyclemv0.registration.SimilarityTransformationComputer;

public class TransformationFactory {

    private RigidTransformationComputer rigidTransformationComputer = new RigidTransformationComputer();
    private SimilarityTransformationComputer similarityTransformationComputer = new SimilarityTransformationComputer();
    private NonRigidTransformationComputer nonRigidTransformationComputer = new NonRigidTransformationComputer();
    private AffineTransformationComputer affineTransformationComputer = new AffineTransformationComputer();

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
