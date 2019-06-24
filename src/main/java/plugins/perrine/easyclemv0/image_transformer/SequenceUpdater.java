package plugins.perrine.easyclemv0.image_transformer;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.model.Transformation;

public class SequenceUpdater {

    private ImageTransformerFactory imageTransformerFactory = new ImageTransformerFactory();

    public void update(Sequence sourceSequence, Transformation transformation) {
        ImageTransformerInterface imageTransformer = imageTransformerFactory.getFrom(transformation.getTransformationType(), transformation.getFiducialSet().getSourceDataset().getDimension());
        imageTransformer.setSourceSequence(sourceSequence);
        imageTransformer.setTargetSize(transformation.getTargetSize());
        imageTransformer.run(transformation.getFiducialSet());
    }
}
