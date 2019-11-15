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
import plugins.perrine.easyclemv0.progress.ProgressTrackableMasterTask;
import plugins.perrine.easyclemv0.transformation.TransformationFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.Transformation;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import javax.inject.Inject;

public class SequenceUpdater extends ProgressTrackableMasterTask implements Runnable {

    private TransformationFactory transformationFactory;
    private DatasetFactory datasetFactory;
    private RoiUpdater roiUpdater;
    private Transformation transformation;
    private Sequence sourceSequence;
    private TransformationSchema transformationSchema;

    public SequenceUpdater(Sequence sourceSequence, TransformationSchema transformationSchema) {
        DaggerSequenceUpdaterComponent.builder().build().inject(this);
        this.sourceSequence = sourceSequence;
        this.transformationSchema = transformationSchema;
    }

    @Override
    public void run() {
        Dataset sourceTransformedDataset = datasetFactory.getFrom(datasetFactory.getFrom(sourceSequence), transformationSchema);
        transformation = transformationFactory.getFrom(transformationSchema);
        Stack3DVTKTransformer imageTransformer = new Stack3DVTKTransformer(sourceSequence, transformationSchema.getTargetSize(), transformation);
        super.add(imageTransformer);
        sourceSequence = imageTransformer.get();
        roiUpdater.updateRoi(sourceTransformedDataset, sourceSequence);
    }

    @Inject
    public void setTransformationFactory(TransformationFactory transformationFactory) {
        this.transformationFactory = transformationFactory;
    }

    @Inject
    public void setDatasetFactory(DatasetFactory datasetFactory) {
        this.datasetFactory = datasetFactory;
    }

    @Inject
    public void setRoiUpdater(RoiUpdater roiUpdater) {
        this.roiUpdater = roiUpdater;
    }
    
    public Transformation getTransformation() {
		return transformation;
    	
    }
}
