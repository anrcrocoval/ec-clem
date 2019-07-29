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
package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceListener;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.sequence_listener.RoiDuplicator;
import plugins.perrine.easyclemv0.sequence_listener.SequenceListenerUtil;

import javax.inject.Inject;
import java.util.List;

import static plugins.perrine.easyclemv0.EasyCLEMv0.Colortab;

public class RoiUpdater {

    private DatasetFactory datasetFactory;
    private RoiFactory roiFactory;
    private SequenceListenerUtil sequenceListenerUtil;

    @Inject
    public RoiUpdater(DatasetFactory datasetFactory, RoiFactory roiFactory, SequenceListenerUtil sequenceListenerUtil) {
        this.datasetFactory = datasetFactory;
        this.roiFactory = roiFactory;
        this.sequenceListenerUtil = sequenceListenerUtil;
    }

    public void updateRoi(Dataset dataset, Sequence sequence) {
        Dataset pixelDataset = datasetFactory.toPixel(dataset, sequence);
        sequence.removeAllROI();
        List<SequenceListener> sequenceListeners = sequenceListenerUtil.removeListeners(sequence, RoiDuplicator.class);
        for(int i = 0; i < pixelDataset.getN(); i++) {
            ROI roi = roiFactory.getFrom(pixelDataset.getPoint(i));
            roi.setName("Point " + (i + 1));
            roi.setColor(Colortab[i % Colortab.length]);
            roi.setStroke(6);
            sequence.addROI(roi);
        }
        sequenceListenerUtil.addListeners(sequence, sequenceListeners);
    }
}
