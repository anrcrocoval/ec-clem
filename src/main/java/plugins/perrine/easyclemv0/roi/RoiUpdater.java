package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceListener;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.RoiFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.sequence_listener.RoiDuplicator;
import plugins.perrine.easyclemv0.util.SequenceListenerUtil;

import java.util.List;

import static plugins.perrine.easyclemv0.EasyCLEMv0.Colortab;

public class RoiUpdater {

    private DatasetFactory datasetFactory = new DatasetFactory();
    private RoiFactory roiFactory = new RoiFactory();
    private SequenceListenerUtil sequenceListenerUtil = new SequenceListenerUtil();

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
