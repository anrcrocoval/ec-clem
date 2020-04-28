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
package plugins.fr.univ_nantes.ec_clem.ec_clem.roi;

import plugins.fr.univ_nantes.ec_clem.ec_clem.error.ellipse.ConfidenceEllipseFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset.DatasetFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence.SequenceSizeFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence_listener.RoiListenerManager;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence_listener.SequenceListenerUtil;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceListener;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;
import java.util.List;

public class RoiUpdater {

    private DatasetFactory datasetFactory;
    private RoiFactory roiFactory;
    private SequenceListenerUtil sequenceListenerUtil;
    private RoiListenerManager roiListenerManager;
    private ConfidenceEllipseFactory confidenceEllipseFactory;
    private SequenceSizeFactory sequenceSizeFactory;

    @Inject
    public RoiUpdater(
        DatasetFactory datasetFactory,
        RoiFactory roiFactory,
        SequenceListenerUtil sequenceListenerUtil,
        RoiListenerManager roiListenerManager,
        ConfidenceEllipseFactory confidenceEllipseFactory,
        SequenceSizeFactory sequenceSizeFactory
    ) {
        this.datasetFactory = datasetFactory;
        this.roiFactory = roiFactory;
        this.sequenceListenerUtil = sequenceListenerUtil;
        this.roiListenerManager = roiListenerManager;
        this.confidenceEllipseFactory = confidenceEllipseFactory;
        this.sequenceSizeFactory = sequenceSizeFactory;
    }

    public void updateRoi(Dataset dataset, Sequence sequence) {
        Dataset pixelDataset = datasetFactory.toPixel(dataset, sequence);
        clear(sequence, dataset.getPointType());
        List<SequenceListener> sequenceListeners = roiListenerManager.removeAll(sequence);
        for(int i = 0; i < pixelDataset.getN(); i++) {
            ROI roi = roiFactory.getRoiFrom(
                roiFactory.getFrom(pixelDataset.getPoint(i)),
                i + 1,
                dataset.getPointType()
            );
            sequence.addROI(roi);
        }
        sequenceListenerUtil.addListeners(sequence, sequenceListeners);
    }

    public void updateErrorRoi(Dataset dataset, TransformationSchema transformationSchema, Sequence sequence) {
        List<SequenceListener> sequenceListeners = roiListenerManager.removeAll(sequence);
        for(int i = 0; i < dataset.getN(); i++) {
            ROI ellipseROI = roiFactory.getFrom(
                confidenceEllipseFactory.getFrom(dataset.getPoint(i), transformationSchema, 0.95),
                sequenceSizeFactory.getFrom(sequence)
            );
            ellipseROI.setName(String.format("%s_%s_%d", PointType.ERROR.name(), dataset.getPointType().name(), i + 1));
            sequence.addROI(ellipseROI);
        }
        sequenceListenerUtil.addListeners(sequence, sequenceListeners);
    }

    public void clear(Sequence sequence, PointType type) {
        sequence.removeROIs(
            roiFactory.getFrom(sequence, type),
            false
        );
    }
}
