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
package plugins.fr.univ_nantes.ec_clem.sequence;

import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import icy.sequence.Sequence;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.DatasetFactory;
import plugins.fr.univ_nantes.ec_clem.progress.ProgressTrackableMasterTask;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.roi.RoiUpdater;

import javax.inject.Inject;

public class SequenceUpdater extends ProgressTrackableMasterTask implements Runnable {

    private RegistrationParameterFactory transformationFactory;
    private DatasetFactory datasetFactory;
    private RoiUpdater roiUpdater;

    private Sequence sourceSequence;
    private TransformationSchema transformationSchema;

    public SequenceUpdater(Sequence sourceSequence, TransformationSchema transformationSchema) {
        DaggerSequenceUpdaterComponent.builder().build().inject(this);
        this.sourceSequence = sourceSequence;
        this.transformationSchema = transformationSchema;
    }

    @Override
    public void run() {
        Dataset sourceTransformedDataset = datasetFactory.getFrom(datasetFactory.getFrom(sourceSequence, PointType.FIDUCIAL), transformationSchema);
        Dataset sourceNonFiducialDataset = datasetFactory.getFrom(sourceSequence, PointType.NOT_FIDUCIAL);
        Dataset sourceNonFiducialTransformedDataset = datasetFactory.getFrom(sourceNonFiducialDataset, transformationSchema);

        Stack3DVTKTransformer imageTransformer = new Stack3DVTKTransformer(
            sourceSequence,
            transformationSchema.getTargetSize(),
            transformationFactory.getFrom(transformationSchema).getTransformation()
        );
        super.add(imageTransformer);
        sourceSequence = imageTransformer.get();

        roiUpdater.updateErrorRoi(sourceNonFiducialDataset, transformationSchema, sourceSequence);
        roiUpdater.updateRoi(sourceTransformedDataset, sourceSequence);
        roiUpdater.updateRoi(sourceNonFiducialTransformedDataset, sourceSequence);
    }

    @Inject
    public void setTransformationFactory(RegistrationParameterFactory transformationFactory) {
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
}
