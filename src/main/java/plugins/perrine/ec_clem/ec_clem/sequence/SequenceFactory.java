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
package plugins.perrine.ec_clem.ec_clem.sequence;

import icy.gui.frame.progress.ProgressFrame;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.vtk.VtkUtil;
import vtk.vtkImageGridSource;
import javax.inject.Inject;
import static icy.type.DataType.UBYTE;

public class SequenceFactory {

    private VtkImageGridSourceFactory vtkImageGridSourceFactory;

    @Inject
    public SequenceFactory(VtkImageGridSourceFactory vtkImageGridSourceFactory) {
        this.vtkImageGridSourceFactory = vtkImageGridSourceFactory;
    }

    public Sequence getMergeSequence(Sequence source, Sequence target) {
        ProgressFrame progressFrame = new ProgressFrame("Merge sequence");
        Sequence result = SequenceUtil.concatC(
            new Sequence[] {
                SequenceUtil.convertToType(source, target.getDataType_(), true),
                target
            },
            false,
            false,
            progressFrame
        );
        result.setName("Merged");
        progressFrame.close();
        return result;
    }

    public Sequence getGridSequence(int xSize, int ySize, int zSize, double spacingX, double spacingY, double spacingZ) {
        vtkImageGridSource sourceGrid = vtkImageGridSourceFactory.getFrom(xSize, ySize, zSize, spacingX, spacingY, spacingZ);
        Sequence grid = new Sequence();
        VtkDataSequenceSupplier vtkDataSequenceSupplier = new VtkDataSequenceSupplier(
            grid,
            UBYTE,
             0,
             1,
            VtkUtil.getJavaArray(sourceGrid.GetOutput().GetPointData().GetScalars()),
            xSize,
            ySize,
            zSize,
            1,
            spacingX,
            spacingY,
            spacingZ
        );
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
