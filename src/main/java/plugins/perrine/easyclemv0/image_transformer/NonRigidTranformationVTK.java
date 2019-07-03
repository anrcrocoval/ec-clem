/**
 * Copyright 2010-2017 Perrine Paul-Gilloteaux, CNRS.
 * Perrine.Paul-Gilloteaux@univ-nantes.fr
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/


package plugins.perrine.easyclemv0.image_transformer;


import icy.sequence.DimensionId;
import icy.vtk.VtkUtil;
import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.viewer.Viewer;
import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import icy.type.DataType;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import vtk.vtkDataArray;
import vtk.vtkDataSet;
import vtk.vtkDoubleArray;
import vtk.vtkFloatArray;
import vtk.vtkImageContinuousDilate3D;
import vtk.vtkImageData;
import vtk.vtkImageGridSource;
import vtk.vtkImageReslice;
import vtk.vtkIntArray;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkShortArray;
import vtk.vtkThinPlateSplineTransform;
import vtk.vtkTransformPolyDataFilter;
import vtk.vtkUnsignedCharArray;
import vtk.vtkUnsignedIntArray;
import vtk.vtkUnsignedShortArray;
import vtk.vtkVertexGlyphFilter;
import static icy.type.DataType.UBYTE;

/**
 *
 * @author paul-gilloteaux-p
 * Transform non rigidly from 2 sets of points, and update the position of ROIs
 */
public class NonRigidTranformationVTK implements ImageTransformerInterface {

    private DatasetFactory datasetFactory = new DatasetFactory();
    private RoiUpdater roiUpdater = new RoiUpdater();

    private  double InputSpacingx ;
    private  double InputSpacingy ;
    private  double InputSpacingz ;
    private Sequence imagesource;
    private Sequence imagetarget;

    private int extentx;
    private int extenty;
    private int extentz;
    private double spacingx;
    private double spacingy;
    private double spacingz;
    private boolean checkgrid;

    private vtkImageGridSource getVtkImageGridSource(int extentx, int extenty, int extentz, double InputSpacingx, double InputSpacingy, double InputSpacingz) {
        vtkImageGridSource sourcegrid = new vtkImageGridSource();
        sourcegrid.SetDataExtent(0, extentx, 0,  extenty, 0,  extentz);
        sourcegrid.SetLineValue(255);
        sourcegrid.SetFillValue(0.0);
        sourcegrid.SetDataScalarType(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR);
        sourcegrid.SetDataSpacing(InputSpacingx, InputSpacingy, InputSpacingz);
        sourcegrid.SetGridSpacing(Math.round(extentx/10), Math.round( extenty/10), Math.round(extentz));
        sourcegrid.Update();
        return sourcegrid;
    }

    private vtkImageData getVtkImageData(vtkImageGridSource sourcegrid, int extentx, int extenty, int extentz) {
        vtkImageData imagedatagrid;
        if (extentz <= 1) {
            vtkImageContinuousDilate3D dilate = new vtkImageContinuousDilate3D();
            dilate.SetInputData(sourcegrid.GetOutput());
            dilate.SetKernelSize(extentx/400,extenty/400, 1);
            dilate.Update();
            imagedatagrid = dilate.GetOutput();
        } else {
            imagedatagrid = sourcegrid.GetOutput();
        }
        return imagedatagrid;
    }

    private vtkImageReslice getVtkImageReslice(vtkImageData imagedatagrid, vtkThinPlateSplineTransform myvtkTransform, int extentx, int extenty, int extentz, double spacingx, double spacingy, double spacingz) {
        vtkImageReslice imageReslicegrid = new vtkImageReslice();
        imageReslicegrid.SetInputData(imagedatagrid);
        imageReslicegrid.SetOutputDimensionality(3);
        imageReslicegrid.SetOutputOrigin(0, 0, 0);
        imageReslicegrid.SetOutputSpacing(spacingx, spacingy, spacingz);
        imageReslicegrid.SetOutputExtent(0, extentx, 0, extenty, 0, extentz); // to be checked: transform is applied twice?
        imageReslicegrid.SetResliceTransform(myvtkTransform.GetInverse());
        imageReslicegrid.SetInterpolationModeToLinear();
        imageReslicegrid.Update();
        return imageReslicegrid;
    }

    private Sequence getSequence(vtkImageData imagedatagrid, int extentx, int extenty, int extentz, double spacingx, double spacingy, double spacingz) {
        int w = extentx + 1;
        int h = extenty + 1;
        int nbz = extentz + 1;
        Sequence grid = new Sequence();
        grid.beginUpdate();
        grid.removeAllImages();
        try {
            for (int z = 0; z < nbz; z++) {
                IcyBufferedImage image = new IcyBufferedImage(w, h,1, UBYTE);
                vtkDataArray myvtkarray = imagedatagrid.GetPointData().GetScalars();
                final byte[] inData = ((vtkUnsignedCharArray) myvtkarray).GetJavaArray();
                byte[] outData = new byte[w * h];
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                    }
                }
                image.setDataXYAsByte(0, outData);
                grid.setImage(0, z, image);
            }
        } finally {
            grid.endUpdate();
        }
        grid.setName("Deformed source grid");
        grid.setPixelSizeX(spacingx);
        grid.setPixelSizeY(spacingy);
        grid.setPixelSizeZ(spacingz);
        return grid;
    }

    private void transformSequence(Sequence imagesource, vtkDataSet[] imageData, int extentx, int extenty, int extentz, double spacingx, double spacingy, double spacingz) {
        DataType datatype = imagesource.getDataType_();
        int w = extentx + 1;
        int h = extenty + 1;
        int nbz = extentz + 1;
        int nbc = imagesource.getSizeC();
        int nbt = imagesource.getSizeT();
        imagesource.beginUpdate();
        imagesource.removeAllImages();
        try {
            switch (datatype) {
                case UBYTE:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final byte[] inData=((vtkUnsignedCharArray) myvtkarray).GetJavaArray();
                                byte[] outData=new byte[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsByte(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                case BYTE:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final byte[] inData=((vtkUnsignedCharArray) myvtkarray).GetJavaArray();
                                byte[] outData=new byte[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsByte(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                case USHORT:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final short[] inData=((vtkUnsignedShortArray) myvtkarray).GetJavaArray();
                                short[] outData=new short[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsShort(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                case UINT:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final int[] inData=((vtkUnsignedIntArray) myvtkarray).GetJavaArray();
                                int[] outData=new int[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsInt(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                case INT:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final int[] inData=((vtkIntArray) myvtkarray).GetJavaArray();
                                int[] outData=new int[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsInt(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                case SHORT:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final short[] inData=((vtkShortArray) myvtkarray).GetJavaArray();
                                short[] outData=new short[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsShort(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                case FLOAT:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final float[] inData=((vtkFloatArray) myvtkarray).GetJavaArray();
                                float[] outData=new float[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsFloat(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                case DOUBLE:
                    for (int t = 0; t < nbt; t++) {
                        for (int z = 0; z < nbz; z++) {
                            IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
                            for (int c=0;c<nbc;c++){
                                vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
                                final double[] inData=((vtkDoubleArray) myvtkarray).GetJavaArray();
                                double[] outData=new double[w*h];
                                for (int i = 0; i < h; i++) {
                                    for (int j = 0; j < w; j++) {
                                        outData[i * w + j] =  inData[z * w * h + i * w + j];
                                    }
                                }
                                image.setDataXYAsDouble(c, outData);
                            }
                            imagesource.setImage(t, z, image);
                        }
                    }
                    break;
                default:
                    System.err.println("unknown data format");
                    break;
            }
            imagesource.setPixelSizeX(spacingx);
            imagesource.setPixelSizeY(spacingy);
            imagesource.setPixelSizeZ(spacingz);
        } finally {
            imagesource.endUpdate();
        }
    }

    private vtkDataSet[] getVtkDataset(Sequence imagesource, vtkThinPlateSplineTransform myvtkTransform, int extentx, int extenty, int extentz, double spacingx, double spacingy, double spacingz, double InputSpacingx, double InputSpacingy, double InputSpacingz) {
        vtkDataSet[] imageData = new vtkDataSet[imagesource.getSizeC()];
        for (int c = 0; c < imagesource.getSizeC(); c++) {
            vtkImageData vtkImageData = convertToVtkImageData(imagesource, InputSpacingx, InputSpacingy, InputSpacingz);
            vtkImageReslice vtkImageReslice = getVtkImageReslice(vtkImageData, myvtkTransform, extentx, extenty, extentz, spacingx, spacingy, spacingz);
            imageData[c] = vtkImageReslice.GetOutput();
        }
        return imageData;
    }

    private vtkPolyData apply(vtkPoints sourcePoints, vtkThinPlateSplineTransform transform) {
        vtkPolyData mypoints = new vtkPolyData();
        mypoints.SetPoints(sourcePoints);

        vtkVertexGlyphFilter vertexfilter = new vtkVertexGlyphFilter();
        vertexfilter.SetInputData(mypoints);
        vertexfilter.Update();

        vtkPolyData sourcepolydata = new vtkPolyData();
        sourcepolydata.ShallowCopy(vertexfilter.GetOutput());

        vtkTransformPolyDataFilter tr = new  vtkTransformPolyDataFilter();
        tr.SetInputData(sourcepolydata);
        tr.SetTransform(transform);
        tr.Update();

        return tr.GetOutput();
    }

    public void run(FiducialSet fiducialSet) {
        vtkPoints lmsource = createVtkPoints(fiducialSet.getSourceDataset());
        vtkPoints lmtarget = createVtkPoints(fiducialSet.getTargetDataset());

//        nonRigidTransformationXmlFileWriter.write(fiducialSet);
//        nonRigidTransformationXmlFileWriter.writeTransformationElements(
//            fiducialSet.getN(),
//            extentx,
//            extenty,
//            extentz,
//            spacingx,
//            spacingy,
//            spacingz,
//            InputSpacingx,
//            InputSpacingy,
//            InputSpacingz
//        );

        final vtkThinPlateSplineTransform myvtkTransform = new vtkThinPlateSplineTransform();
        myvtkTransform.SetSourceLandmarks(lmsource);
        myvtkTransform.SetTargetLandmarks(lmtarget);

        if (extentz <= 1){
            myvtkTransform.SetBasisToR2LogR();
        } else{
            myvtkTransform.SetBasisToR();
        }

        System.out.println("Starting to non rigidly register " + imagesource.getFilename() + " on " + imagetarget.getFilename());

        if (checkgrid) {
            vtkImageGridSource sourcegrid = getVtkImageGridSource(extentx, extenty, extentz, InputSpacingx, InputSpacingy, InputSpacingz);
            vtkImageData imagedatagrid = getVtkImageData(sourcegrid, extentx, extenty, extentz);
            vtkImageReslice imageReslicegrid = getVtkImageReslice(imagedatagrid, myvtkTransform, extentx, extenty, extentz, spacingx, spacingy, spacingz);
            imagedatagrid = imageReslicegrid.GetOutput();
            final Sequence grid = getSequence(imagedatagrid, extentx, extenty, extentz, spacingx, spacingy, spacingz);

            ThreadUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new Viewer(grid);

                }
            });
        }

        vtkDataSet[] imageData = getVtkDataset(imagesource, myvtkTransform, extentx, extenty, extentz, spacingx, spacingy, spacingz, InputSpacingx, InputSpacingy, InputSpacingz);
        transformSequence(imagesource, imageData, extentx, extenty, extentz, spacingx, spacingy, spacingz);

        new AnnounceFrame("Non Rigid Transformation Updated",5);


        Dataset sourceImageDataset = datasetFactory.getFrom(imagesource);
        vtkPolyData modifiedpoints = apply(createVtkPoints(sourceImageDataset), myvtkTransform);

        for (int p = 0; p < modifiedpoints.GetNumberOfPoints(); p++) {
            double[] newpos = modifiedpoints.GetPoint(p);

            sourceImageDataset.getMatrix().set(p, 0, newpos[0] / this.spacingx);
            sourceImageDataset.getMatrix().set(p, 1, newpos[1] / this.spacingx);
            sourceImageDataset.getMatrix().set(p, 2, newpos[2] / this.spacingx);
        }

        roiUpdater.updateRoi(sourceImageDataset, imagesource);
        System.out.println("have been applied");
    }

//    public void setFiducialSet(FiducialSet fiducialSet) {
//        this.fiducialSet = fiducialSet;
//    }

    public void setCheckgrid(boolean checkgrid) {
        this.checkgrid = checkgrid;
    }

    @Override
    public void setSourceSequence(Sequence sequence) {
        this.imagesource = sequence;
        setSourceSize(sequence.getPixelSizeX(), sequence.getPixelSizeY(), sequence.getPixelSizeZ());
    }

    public void setSourceSize(double pixelSizeX, double pixelSizeY, double pixelSizeZ) {
        this.InputSpacingx = pixelSizeX;
        this.InputSpacingy = pixelSizeY;
        this.InputSpacingz = pixelSizeZ;
    }

    public void setTargetSequence(Sequence sequence) {
        this.imagetarget = sequence;
        setTargetSize(sequence.getSizeX(), sequence.getSizeY(), sequence.getSizeZ(), sequence.getPixelSizeX(), sequence.getPixelSizeY(), sequence.getPixelSizeZ());
    }

    @Override
    public void setTargetSize(Sequence sequence) {
        setTargetSize(
            sequence.getSizeX(),
            sequence.getSizeY(),
            sequence.getSizeZ(),
            sequence.getPixelSizeX(),
            sequence.getPixelSizeY(),
            sequence.getPixelSizeZ()
        );
    }

    @Override
    public void setTargetSize(SequenceSize targetSize) {
        setTargetSize(
            targetSize.get(DimensionId.X).getSize(),
            targetSize.get(DimensionId.Y).getSize(),
            targetSize.get(DimensionId.Z).getSize(),
            targetSize.get(DimensionId.X).getPixelSizeInMicrometer(),
            targetSize.get(DimensionId.Y).getPixelSizeInMicrometer(),
            targetSize.get(DimensionId.Z).getPixelSizeInMicrometer()
        );
    }

    public void setTargetSize(int sizeX, int sizeY, int sizeZ, double pixelSizeX, double pixelSizeY, double pixelSizeZ) {
        this.extentx = sizeX - 1;
        this.extenty = sizeY - 1;
        this.extentz = sizeZ - 1;
        this.spacingx = pixelSizeX;
        this.spacingy = pixelSizeY;
        this.spacingz = pixelSizeZ;
    }

    private vtkPoints createVtkPoints(Dataset dataset) {
        vtkPoints points = new vtkPoints();
        points.SetNumberOfPoints(dataset.getN());
        for (int i = 0; i < dataset.getN(); i++) {
            points.SetPoint(i,dataset.getPoint(i).getMatrix().transpose().getArray()[0]);
        }
        return points;
    }

    private vtkImageData convertToVtkImageData(Sequence imagesource, double InputSpacingx, double InputSpacingy, double InputSpacingz) {
        if (imagesource == null)
            return null;

        final int sizeX = imagesource.getSizeX();
        final int sizeY = imagesource.getSizeY();
        final int sizeZ = imagesource.getSizeZ();
        final int posC = imagesource.getSizeC();
        final int posT = 0;
        final DataType dataType = imagesource.getDataType_();

        final vtkImageData newImageData = new vtkImageData();
        newImageData.SetDimensions(sizeX, sizeY, sizeZ);
        newImageData.SetSpacing(InputSpacingx, InputSpacingy, InputSpacingz);

        vtkDataArray array;
        switch (dataType) {
            case UBYTE:
                newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkUnsignedCharArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsByte(posT));
                else
                    ((vtkUnsignedCharArray) array).SetJavaArray(imagesource.getDataCopyXYZAsByte(posT, posC));
                break;
            case BYTE:
                newImageData.AllocateScalars(VtkUtil.VTK_CHAR, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkUnsignedCharArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsByte(posT));
                else
                    ((vtkUnsignedCharArray) array).SetJavaArray(imagesource.getDataCopyXYZAsByte(posT, posC));
                break;
            case USHORT:
                newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_SHORT, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkUnsignedShortArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsShort(posT));
                else
                    ((vtkUnsignedShortArray) array).SetJavaArray(imagesource.getDataCopyXYZAsShort(posT, posC));
                break;
            case SHORT:
                newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_SHORT, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkShortArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsShort(posT));
                else
                    ((vtkShortArray) array).SetJavaArray(imagesource.getDataCopyXYZAsShort(posT, posC));
                break;
            case UINT:
                newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_INT, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkUnsignedIntArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsInt(posT));
                else
                    ((vtkUnsignedIntArray) array).SetJavaArray(imagesource.getDataCopyXYZAsInt(posT, posC));
                break;

            case INT:
                newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_INT, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkIntArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsInt(posT));
                else
                    ((vtkIntArray) array).SetJavaArray(imagesource.getDataCopyXYZAsInt(posT, posC));
                break;

            case FLOAT:
                newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_FLOAT, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkFloatArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsFloat(posT));
                else
                    ((vtkFloatArray) array).SetJavaArray(imagesource.getDataCopyXYZAsFloat(posT, posC));
                break;

            case DOUBLE:
                newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_DOUBLE, 1);
                array = newImageData.GetPointData().GetScalars();
                if (posC == -1)
                    ((vtkDoubleArray) array).SetJavaArray(imagesource.getDataCopyCXYZAsDouble(posT));
                else
                    ((vtkDoubleArray) array).SetJavaArray(imagesource.getDataCopyXYZAsDouble(posT, posC));
                break;

            default:
                // we probably have an empty sequence
                newImageData.SetDimensions(1, 1, 1);
                newImageData.SetSpacing(imagesource.getPixelSizeX(), imagesource.getPixelSizeY(), imagesource.getPixelSizeZ());
                newImageData.SetNumberOfScalarComponents(1, null);
                newImageData.SetExtent(0, 0, 0, 0, 0, 0);
                newImageData.AllocateScalars(null);
                break;
        }

        // release previous volume data memory
//        if (imageData[posC] != null) {
//            final vtkPointData pointData = imageData[posC].GetPointData();
//            if (pointData != null) {
//                final vtkDataArray dataArray = pointData.GetScalars();
//                if (dataArray != null)
//                    dataArray.Delete();
//                pointData.Delete();
//                imageData[posC].ReleaseData();
//                imageData[posC].Delete();
//            }
//        }

//        imageData[posC] = newImageData;
        return newImageData;
    }

}
