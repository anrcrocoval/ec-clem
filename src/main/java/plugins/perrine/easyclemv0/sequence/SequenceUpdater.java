/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.easyclemv0.sequence;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.transformation.TransformationFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.Transformation;
import plugins.perrine.easyclemv0.roi.RoiUpdater;

import javax.inject.Inject;

public class SequenceUpdater {

    private Stack3DVTKTransformer imageTransformer;
    private TransformationFactory transformationFactory;
    private DatasetFactory datasetFactory;
    private RoiUpdater roiUpdater;

    @Inject
    public SequenceUpdater(Stack3DVTKTransformer imageTransformer, TransformationFactory transformationFactory, DatasetFactory datasetFactory, RoiUpdater roiUpdater) {
        this.imageTransformer = imageTransformer;
        this.transformationFactory = transformationFactory;
        this.datasetFactory = datasetFactory;
        this.roiUpdater = roiUpdater;
    }

    public void update(Sequence sourceSequence, TransformationSchema transformationSchema) {
        Transformation transformation = transformationFactory.getFrom(transformationSchema);
        Dataset sourceTransformedDataset = datasetFactory.getFrom(datasetFactory.getFrom(sourceSequence), transformationSchema);
        imageTransformer.setSourceSequence(sourceSequence);
        imageTransformer.setTargetSize(transformationSchema.getTargetSize());
        imageTransformer.run(transformation);
        roiUpdater.updateRoi(sourceTransformedDataset, sourceSequence);
    }
}
