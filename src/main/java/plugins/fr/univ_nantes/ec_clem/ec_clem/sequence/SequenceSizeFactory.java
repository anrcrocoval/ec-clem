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
package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence;

import icy.sequence.DimensionId;
import icy.sequence.Sequence;

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
    public SequenceSize getFrom(DimensionSize sizeX,DimensionSize sizeY, DimensionSize sizeZ) {
        SequenceSize sequenceSize = new SequenceSize();

            sequenceSize.add(sizeX);
        

        
            sequenceSize.add(sizeY);
        

            sequenceSize.add(sizeZ);

        return sequenceSize;
    }
}
