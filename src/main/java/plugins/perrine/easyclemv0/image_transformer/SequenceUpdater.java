package plugins.perrine.easyclemv0.image_transformer;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.TransformationFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.TransformationSchema;
import plugins.perrine.easyclemv0.model.transformation.Transformation;
import plugins.perrine.easyclemv0.roi.RoiUpdater;

public class SequenceUpdater {

    private ImageTransformer imageTransformer = new Stack3DVTKTransformer();
    private TransformationFactory transformationFactory = new TransformationFactory();
    private DatasetFactory datasetFactory = new DatasetFactory();
    private RoiUpdater roiUpdater = new RoiUpdater();

    public void update(Sequence sourceSequence, TransformationSchema transformationSchema) {
        Transformation transformation = transformationFactory.getFrom(transformationSchema);
        Dataset sourceTransformedDataset = datasetFactory.getFrom(datasetFactory.getFrom(sourceSequence), transformationSchema);
        imageTransformer.setSourceSequence(sourceSequence);
        imageTransformer.setTargetSize(transformationSchema.getTargetSize());
        imageTransformer.run(transformation);
        roiUpdater.updateRoi(sourceTransformedDataset, sourceSequence);
    }
}
