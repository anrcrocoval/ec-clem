package plugins.perrine.easyclemv0.image_transformer;

import icy.gui.viewer.Viewer;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.SequenceFactory;
import plugins.perrine.easyclemv0.factory.TransformationFactory;
import plugins.perrine.easyclemv0.model.TransformationSchema;

public class SequenceUpdater {

    private ImageTransformer imageTransformer = new Stack3DVTKTransformer();
    private TransformationFactory transformationFactory = new TransformationFactory();
    private SequenceFactory sequenceFactory = new SequenceFactory();

    public void update(Sequence sourceSequence, TransformationSchema transformationSchema) {
        new Viewer(
                sequenceFactory.getGridSequence(
                        sourceSequence.getSizeX(),
                        sourceSequence.getSizeY(),
                        sourceSequence.getSizeZ(),
                        sourceSequence.getPixelSizeX(),
                        sourceSequence.getPixelSizeY(),
                        sourceSequence.getPixelSizeZ()
                ));

        imageTransformer.setSourceSequence(sourceSequence);
        imageTransformer.setTargetSize(transformationSchema.getTargetSize());
        imageTransformer.run(transformationFactory.getFrom(transformationSchema));
    }
}
