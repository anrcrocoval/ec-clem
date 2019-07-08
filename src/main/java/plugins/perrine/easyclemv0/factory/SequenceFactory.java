package plugins.perrine.easyclemv0.factory;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.type.DataType;
import plugins.perrine.easyclemv0.factory.vtk.VtkImageGridSourceFactory;
import vtk.*;
import java.lang.reflect.Array;
import static icy.type.DataType.UBYTE;

public class SequenceFactory {

    private VtkImageGridSourceFactory vtkImageGridSourceFactory = new VtkImageGridSourceFactory();

    public Sequence getMergeSequence(Sequence source, Sequence target) {
        Sequence result1 = SequenceUtil.extractSlice(source, source.getFirstViewer().getPositionZ());
        result1 = SequenceUtil.extractFrame(result1, source.getFirstViewer().getPositionT());

        Sequence result2;
        if (target.getSizeZ() >= source.getSizeZ()) {
            result2 = SequenceUtil.extractSlice(target, source.getFirstViewer().getPositionZ());
        } else {
            result2 = SequenceUtil.extractSlice(target, target.getFirstViewer().getPositionZ());
        }
        result2 = SequenceUtil.extractFrame(result2, target.getFirstViewer().getPositionT());

        if (result1.getDataType_() != result2.getDataType_()) {
            result2 = SequenceUtil.convertToType(result2, result1.getDataType_(), true);
        }

        Sequence[] sequences = new Sequence[result1.getSizeC() + result2.getSizeC()];
        for (int c = 0; c < result1.getSizeC(); c++) {
            sequences[c] = SequenceUtil.extractChannel(result1, c);
        }

        for (int c = result1.getSizeC(); c < result1.getSizeC() + result2.getSizeC(); c++) {
            sequences[c] = SequenceUtil.extractChannel(result2, c - result1.getSizeC());
        }

        int[] channels = new int[sequences.length];
        Sequence result = SequenceUtil.concatC(sequences, channels, false, false, null);

        result.setName("Merged");

        return result;
    }

    public Sequence getGridSequence(int xSize, int ySize, int zSize, double spacingX, double spacingY, double spacingZ) {
        vtkImageGridSource sourceGrid = vtkImageGridSourceFactory.getFrom(xSize, ySize, zSize, spacingX, spacingY, spacingZ);
        Sequence grid = new Sequence();
        grid.setImage(0, 0, new IcyBufferedImage(xSize, ySize, 1, UBYTE));
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
            for (int t = 0; t < tSize; t++) {
                for (int z = 0; z < zSize; z++) {
                    sequence.setImage(t, z, getImage(
                            vtkDataSetArray, dataType, xSize, ySize, z, t, channels
                    ));
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

    private IcyBufferedImage getImage(vtkDataSet[] vtkDataSetArray, DataType dataType, int xSize, int ySize, int z, int t, int channels) {
        IcyBufferedImage image = new IcyBufferedImage(xSize, ySize, channels, dataType);
        for (int c = 0; c < channels; c++) {
            vtkDataArray dataArray = vtkDataSetArray[c].GetPointData().GetScalars();
            Object inData = getPrimitiveArray(dataType, dataArray);
            Object outData = Array.newInstance(dataType.toPrimitiveClass(), xSize * ySize);
            System.arraycopy(inData, z * t * xSize * ySize, outData, 0, xSize * ySize);
            image.setDataXY(c, outData);
        }
        return image;
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
