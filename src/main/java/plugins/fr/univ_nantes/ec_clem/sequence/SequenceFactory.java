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

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import vtk.*;
import javax.inject.Inject;
import static icy.type.DataType.UBYTE;

public class SequenceFactory {

    private VtkImageGridSourceFactory vtkImageGridSourceFactory;

    @Inject
    public SequenceFactory(VtkImageGridSourceFactory vtkImageGridSourceFactory) {
        this.vtkImageGridSourceFactory = vtkImageGridSourceFactory;
    }

    public Sequence getMergeSequence(Sequence source, Sequence target) {
        Sequence result = SequenceUtil.concatC(
            new Sequence[] {
                SequenceUtil.convertToType(source, target.getDataType_(), false),
                target
            },
            false,
            false,
            null
        );
        result.setName("Merged");
        return result;
    }

    public Sequence getGridSequence(int xSize, int ySize, int zSize, double spacingX, double spacingY, double spacingZ) {
        vtkImageGridSource sourceGrid = vtkImageGridSourceFactory.getFrom(xSize, ySize, zSize, spacingX, spacingY, spacingZ);
        Sequence grid = new Sequence();
        for(int z = 0; z < zSize; z++) {
            grid.setImage(0, z, new IcyBufferedImage(xSize, ySize, 1, UBYTE));
        }
        VtkDataSequenceSupplier vtkDataSequenceSupplier = new VtkDataSequenceSupplier(grid, new vtkPointData[] { sourceGrid.GetOutput().GetPointData() }, xSize, ySize, zSize, grid.getSizeT(), spacingX, spacingY, spacingZ);
        grid = vtkDataSequenceSupplier.get();
        grid.setName("Grid");
        return grid;
    }

    public Sequence getFrom(SequenceSize sequenceSize) {
        Sequence newSequence = new Sequence();
        for(DimensionSize dimensionSize : sequenceSize.getDimensions()) {
            switch(dimensionSize.getDimensionId()) {
                case X: newSequence.setPixelSizeX(dimensionSize.getPixelSizeInMicrometer());
                    break;
                case Y: newSequence.setPixelSizeY(dimensionSize.getPixelSizeInMicrometer());
                    break;
                case Z: newSequence.setPixelSizeZ(dimensionSize.getPixelSizeInMicrometer());
                    break;
            }
        }
        return newSequence;
    }
}
