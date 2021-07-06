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

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.type.DataType;
import plugins.perrine.ec_clem.ec_clem.progress.ProgressTrackableChildTask;

import java.lang.reflect.Array;
import java.util.function.Supplier;

public class VtkDataSequenceSupplier extends ProgressTrackableChildTask implements Supplier<Sequence> {

    private Sequence sequence;

    private Object vtkDataSetArray;
    private int xSize;
    private int ySize;
    private int zSize;
    private int tSize;
    private int cSize;
    private int channel;
    private double spacingX;
    private double spacingY;
    private double spacingZ;
    private DataType dataType;
    private int c;
    private int sizeC;

    public VtkDataSequenceSupplier(Sequence sequence, DataType datatype, int c, int sizeC, Object vtkDataSetArray, int xSize, int ySize, int zSize, int tSize, double spacingX, double spacingY, double spacingZ) {
        super(tSize * zSize);
        this.sequence = sequence;
        this.dataType = dataType;
        this.vtkDataSetArray = vtkDataSetArray;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.tSize = tSize;
        this.cSize = cSize;
        this.channel = channel;
        this.spacingX = spacingX;
        this.spacingY = spacingY;
        this.spacingZ = spacingZ;
        this.dataType = datatype;
        this.c = c;
        this.sizeC = sizeC;
    }

    @Override
    public Sequence get() {
        sequence.beginUpdate();
        try {
            Object inData = vtkDataSetArray;
            Object outData = Array.newInstance(dataType.toPrimitiveClass(), xSize * ySize);
            for (int t = 0; t < tSize; t++) {
                for (int z = 0; z < zSize; z++) {
                    IcyBufferedImage image = sequence.getImage(t, z);
                    if(image == null) {
                        image = new IcyBufferedImage(xSize, ySize, sizeC, dataType);
                        sequence.setImage(t, z, image);
                        image.setVolatile(sequence.isVolatile());
                    }

                    System.arraycopy(inData, (t * zSize * xSize * ySize) + (z * xSize * ySize), outData, 0, xSize * ySize);
                    image.setDataXY(c, outData);
                    super.incrementCompleted();
                }
            }
            sequence.setPixelSizeX(spacingX);
            sequence.setPixelSizeY(spacingY);
            sequence.setPixelSizeZ(spacingZ);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sequence.endUpdate();
        }
        vtkDataSetArray = null;
        return sequence;
    }

    public void setData(Object vtkDataSetArray) {
        this.vtkDataSetArray = vtkDataSetArray;
    }
}
