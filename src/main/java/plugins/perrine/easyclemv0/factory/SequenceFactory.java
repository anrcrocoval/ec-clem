package plugins.perrine.easyclemv0.factory;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.type.DataType;
import plugins.perrine.easyclemv0.factory.vtk.VtkImageGridSourceFactory;
import vtk.*;

import javax.inject.Inject;
import java.lang.reflect.Array;
import static icy.type.DataType.UBYTE;

public class SequenceFactory {

    private VtkImageGridSourceFactory vtkImageGridSourceFactory;

    @Inject
    public SequenceFactory(VtkImageGridSourceFactory vtkImageGridSourceFactory) {
        this.vtkImageGridSourceFactory = vtkImageGridSourceFactory;
    }

    public Sequence getMergeSequence(Sequence source, Sequence target) {
        Sequence result = SequenceUtil.concatC(new Sequence[] { source, target }, false, false, null);
        result.setName("Merged");
        return result;
    }

    public Sequence getGridSequence(int xSize, int ySize, int zSize, double spacingX, double spacingY, double spacingZ) {
        vtkImageGridSource sourceGrid = vtkImageGridSourceFactory.getFrom(xSize, ySize, zSize, spacingX, spacingY, spacingZ);
        Sequence grid = new Sequence();
        for(int z = 0; z < zSize; z++) {
            grid.setImage(0, z, new IcyBufferedImage(xSize, ySize, 1, UBYTE));
        }
        grid = getFrom(grid, new vtkDataSet[] { sourceGrid.GetOutput() }, xSize, ySize, zSize, grid.getSizeT(), spacingX, spacingY, spacingZ);
        grid.setName("Grid");
        return grid;
    }

    public Sequence getFrom(Sequence sequence, vtkDataSet[] vtkDataSetArray, int xSize, int ySize, int zSize, int tSize, double spacingX, double spacingY, double spacingZ) {
        int channels = sequence.getSizeC();
        DataType dataType = sequence.getDataType_();
        sequence.beginUpdate();
        sequence.removeAllImages();
        try {
            for (int c = 0; c < channels; c++) {
                Object inData = getPrimitiveArray(dataType, vtkDataSetArray[c].GetPointData().GetScalars());
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
