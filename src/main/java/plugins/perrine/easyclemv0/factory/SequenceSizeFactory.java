package plugins.perrine.easyclemv0.factory;

import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.model.DimensionSize;
import plugins.perrine.easyclemv0.model.SequenceSize;

import javax.inject.Inject;

public class SequenceSizeFactory {

    @Inject
    public SequenceSizeFactory() {}

    public SequenceSize getFrom(Sequence sequence) {
        SequenceSize sequenceSize = new SequenceSize();

        if(sequence.getSizeX() > 0) {
            sequenceSize.add(new DimensionSize(DimensionId.X, sequence.getSizeX(), sequence.getPixelSizeX()));
        }

        if(sequence.getSizeY() > 0) {
            sequenceSize.add(new DimensionSize(DimensionId.Y, sequence.getSizeY(), sequence.getPixelSizeY()));
        }

        if(sequence.getSizeZ() > 0) {
            sequenceSize.add(new DimensionSize(DimensionId.Z, sequence.getSizeZ(), sequence.getPixelSizeZ()));
        }

        return sequenceSize;
    }
}
