package plugins.perrine.easyclemv0.sequence;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.type.DataType;
import plugins.perrine.easyclemv0.progress.ProgressTrackableChildTask;
import vtk.*;

import java.lang.reflect.Array;
import java.util.function.Supplier;

public class VtkDataSequenceSupplier extends ProgressTrackableChildTask implements Supplier<Sequence> {

    private Sequence sequence;
    private vtkPointData[] vtkDataSetArray;
    private int xSize;
    private int ySize;
    private int zSize;
    private int tSize;
    private double spacingX;
    private double spacingY;
    private double spacingZ;

    public VtkDataSequenceSupplier(Sequence sequence, vtkPointData[] vtkDataSetArray, int xSize, int ySize, int zSize, int tSize, double spacingX, double spacingY, double spacingZ) {
        super(sequence.getSizeC() * tSize * zSize);
        DaggerVtkDataSequenceSupplierComponent.builder().build().inject(this);
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
        int channels = sequence.getSizeC();
        DataType dataType = sequence.getDataType_();
        sequence.beginUpdate();
        sequence.removeAllImages();
        try {
            for (int c = 0; c < channels; c++) {
                Object inData = getPrimitiveArray(dataType, vtkDataSetArray[c].GetScalars());
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

    private Object getPrimitiveArray(DataType datatype, vtkDataArray dataArray) {
        switch(datatype) {
            case UBYTE:
                return ((vtkUnsignedCharArray) dataArray).GetJavaArray();
            case BYTE:
                return ((vtkUnsignedCharArray) dataArray).GetJavaArray();
            case USHORT:
                return ((vtkUnsignedShortArray) dataArray).GetJavaArray();
            case SHORT:
                return ((vtkShortArray) dataArray).GetJavaArray();
            case INT:
                return ((vtkIntArray) dataArray).GetJavaArray();
            case UINT:
                return ((vtkUnsignedIntArray) dataArray).GetJavaArray();
            case FLOAT:
                return ((vtkFloatArray) dataArray).GetJavaArray();
            case DOUBLE:
                return ((vtkDoubleArray) dataArray).GetJavaArray();
            default : throw new RuntimeException("Unsupported type");
        }
    }
}