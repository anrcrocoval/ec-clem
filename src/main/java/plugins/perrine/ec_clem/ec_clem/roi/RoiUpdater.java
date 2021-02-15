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
package plugins.perrine.ec_clem.ec_clem.roi;

import icy.canvas.IcyCanvas;
import icy.gui.viewer.Viewer;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.ConfidenceEllipseFactory;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.DatasetFactory;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceSizeFactory;
import plugins.perrine.ec_clem.ec_clem.sequence_listener.RoiListenerManager;
import plugins.perrine.ec_clem.ec_clem.sequence_listener.SequenceListenerUtil;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceListener;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;
import java.util.ArrayList;
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
        try {
            for(int i = 0; i < dataset.getN(); i++) {
                ROI ellipseROI = roiFactory.getFrom(
                    confidenceEllipseFactory.getFrom(dataset.getPoint(i), transformationSchema, 0.95),
                    sequenceSizeFactory.getFrom(sequence)
                );
                ellipseROI.setName(String.format("%s_%s_%d", PointType.PREDICTED_ERROR.name(), dataset.getPointType().name(), i + 1));
                sequence.addROI(ellipseROI);
            }
        } finally {
            sequenceListenerUtil.addListeners(sequence, sequenceListeners);
        }
    }

    public void updateMeasuredErrorRoi(Dataset sourceDataset, Dataset targetDataset, Sequence sequence) {
        assert sourceDataset.getDimension() == targetDataset.getDimension();
        List<SequenceListener> sequenceListeners = roiListenerManager.removeAll(sequence);
        for(int i = 0; i < sourceDataset.getN(); i++) {
            ROI lineROI = roiFactory.getFrom(
                sourceDataset.getPoint(i),
                targetDataset.getPoint(i)
            );
            lineROI.setName(String.format("%s_%s_%d", PointType.MEASURED_ERROR.name(), sourceDataset.getPointType().name(), i + 1));
            sequence.addROI(lineROI);
        }
        sequenceListenerUtil.addListeners(sequence, sequenceListeners);
    }

    public void clear(Sequence sequence, PointType type) {
        sequence.removeROIs(
            roiFactory.getFrom(sequence, type),
            false
        );
    }

    public void setLayersVisible(Sequence sequence, PointType pointType, boolean visible) {
        List<ROI> roiList = roiFactory.getFrom(sequence, pointType);
        for(ROI roi : roiList) {
            setLayersVisible(roi, visible);
        }
    }

    private void setLayersVisible(ROI roi, boolean visible) {
        List<IcyCanvas> attachedCanvas = roi.getOverlay().getAttachedCanvas();
        for(IcyCanvas canvas : attachedCanvas) {
            canvas.getLayer(roi).setVisible(visible);
        }
    }
}
