package plugins.perrine.easyclemv0.image_transformer;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.TransformationFactory;
import plugins.perrine.easyclemv0.model.TransformationSchema;

public class SequenceUpdater {

    private ImageTransformerInterface imageTransformer = new Stack3DVTKTransformer();
    private TransformationFactory transformationFactory = new TransformationFactory();

    public void update(Sequence sourceSequence, TransformationSchema transformationSchema) {
        imageTransformer.setSourceSequence(sourceSequence);
        imageTransformer.setTargetSize(transformationSchema.getTargetSize());
        imageTransformer.run(transformationFactory.getFrom(transformationSchema));
    }
}
