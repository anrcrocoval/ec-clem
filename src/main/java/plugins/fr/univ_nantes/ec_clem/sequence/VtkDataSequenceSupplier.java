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
import icy.type.DataType;
import plugins.fr.univ_nantes.ec_clem.progress.ProgressTrackableChildTask;
import java.lang.reflect.Array;
import java.util.function.Supplier;

public class VtkDataSequenceSupplier extends ProgressTrackableChildTask implements Supplier<Sequence> {

    private Sequence sequence;
    private Object[] vtkDataSetArray;
    private int xSize;
    private int ySize;
    private int zSize;
    private int tSize;
    private double spacingX;
    private double spacingY;
    private double spacingZ;

    public VtkDataSequenceSupplier(Sequence sequence, Object[] vtkDataSetArray, int xSize, int ySize, int zSize, int tSize, double spacingX, double spacingY, double spacingZ) {
        super(vtkDataSetArray.length * tSize * zSize);
        this.sequence = sequence;
        this.vtkDataSetArray = vtkDataSetArray;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.tSize = tSize;
        this.spacingX = spacingX;
        this.spacingY = spacingY;
        this.spacingZ = spacingZ;
    }

    @Override
    public Sequence get() {
        int channels = vtkDataSetArray.length;
        DataType dataType = sequence.getDataType_();
        sequence.beginUpdate();
        sequence.removeAllImages();
        try {
            for (int c = 0; c < channels; c++) {
                Object inData = vtkDataSetArray[c];
                for (int t = 0; t < tSize; t++) {
                    for (int z = 0; z < zSize; z++) {
                        IcyBufferedImage image = sequence.getImage(t, z);
                        if(image == null) {
                            image = new IcyBufferedImage(xSize, ySize, channels, dataType);
                            sequence.setImage(t, z, image);
                        }
                        Object outData = Array.newInstance(dataType.toPrimitiveClass(), xSize * ySize);
                        System.arraycopy(inData, (t * zSize * xSize * ySize) + (z * xSize * ySize), outData, 0, xSize * ySize);
                        image.setDataXY(c, outData);
                        super.incrementCompleted();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sequence.endUpdate();
        }
        sequence.setPixelSizeX(spacingX);
        sequence.setPixelSizeY(spacingY);
        sequence.setPixelSizeZ(spacingZ);
        return sequence;
    }
}
