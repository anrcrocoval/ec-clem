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

import icy.gui.frame.progress.ProgressFrame;
import icy.image.IcyBufferedImage;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import icy.type.DataType;
import Jama.Matrix;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.model.Similarity;
import plugins.perrine.easyclemv0.registration.RigidTransformationComputer;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import vtk.vtkDataArray;
import vtk.vtkDataSet;
import vtk.vtkDoubleArray;
import vtk.vtkFloatArray;
import vtk.vtkImageData;
import vtk.vtkImageReslice;
import vtk.vtkIntArray;
import vtk.vtkMatrix4x4;
import vtk.vtkPointData;
import vtk.vtkShortArray;
import vtk.vtkTransform;
import vtk.vtkUnsignedCharArray;
import vtk.vtkUnsignedIntArray;
import vtk.vtkUnsignedShortArray;

/**
 * The difference with 2D transform is that the tranform is computed in REAL UNITS, because vtk apply it in real unit,
 * which can be quite convenient for dealing with anisotropy!
 */
public class Stack3DVTKTransformer implements ImageTransformerInterface {

	private vtkImageReslice ImageReslice;
	private vtkMatrix4x4 transfo3D;
	private Sequence sequence;
	private vtkDataSet[] imageData;
	private int extentx;
	private int extenty;
	private int extentz;
	private double spacingx;
	private double spacingy;
	private double spacingz;
	private double InputSpacingz;
	private double InputSpacingx;
	private double InputSpacingy;

	private RigidTransformationComputer rigidTransformationComputer = new RigidTransformationComputer();
	private RoiUpdater roiUpdater = new RoiUpdater();
	private DatasetFactory datasetFactory = new DatasetFactory();

	public void setSourceSequence(Sequence sequence) {
		this.sequence = sequence;
		setSourceSize(sequence.getPixelSizeX(), sequence.getPixelSizeY(), sequence.getPixelSizeZ());
	}

	public void setSourceSize(double pixelSizeX, double pixelSizeY, double pixelSizeZ) {
		InputSpacingx = pixelSizeX;
		InputSpacingy = pixelSizeY;
		InputSpacingz = pixelSizeZ;
	}

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

	public void setTargetSize(SequenceSize sequenceSize) {
		setTargetSize(
			sequenceSize.get(DimensionId.X).getSize(),
			sequenceSize.get(DimensionId.Y).getSize(),
			sequenceSize.get(DimensionId.Z).getSize(),
			sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer(),
			sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer(),
			sequenceSize.get(DimensionId.Z).getPixelSizeInMicrometer()
		);
	}

	public void setTargetSize(int sizeX, int sizeY, int sizeZ, double pixelSizeX, double pixelSizeY, double pixelSizeZ) {
		this.extentx = sizeX;
		this.extenty = sizeY;
		this.extentz = sizeZ;
		this.spacingx = pixelSizeX;
		this.spacingy = pixelSizeY;
		this.spacingz = pixelSizeZ;
	}

	private void setParameters(Matrix transformationMatrix) {
		if (transformationMatrix.getRowDimension() != 4) {
			throw new RuntimeException("Use this class for 3D transformation only");
		}

		transfo3D = new vtkMatrix4x4();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				transfo3D.SetElement(i, j, transformationMatrix.get(i, j));
			}
		}
	}

	public void run(FiducialSet fiducialSet) {

		Similarity similarity = rigidTransformationComputer.compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
		Dataset sourceTransformedDataset = similarity.apply(datasetFactory.getFrom(sequence));
		setParameters(similarity.getMatrix());

		System.out.println("I will apply transfo now");
		ProgressFrame progress = new ProgressFrame("Applying the transformation...");

		vtkTransform mytransfo = new vtkTransform();
		mytransfo.SetMatrix(transfo3D);
		ImageReslice = new vtkImageReslice();
		ImageReslice.SetOutputDimensionality(3);
		ImageReslice.SetOutputOrigin(0, 0, 0);
		ImageReslice.SetOutputSpacing(spacingx, spacingy, spacingz);
		ImageReslice.SetOutputExtent(0, extentx - 1, 0, extenty - 1, 0, extentz - 1);
		ImageReslice.SetResliceTransform(mytransfo.GetInverse());
		ImageReslice.SetInterpolationModeToLinear();

		imageData = new vtkDataSet[sequence.getSizeC()];
		for (int c = 0; c < sequence.getSizeC(); c++) {
			converttoVtkImageData(c);
			ImageReslice.SetInputData(imageData[c]);
			ImageReslice.Update();
			imageData[c] = ImageReslice.GetOutput();
		}

		int nbc = sequence.getSizeC();
		int nbt = sequence.getSizeT();
		int nbz = extentz;
		int w = extentx;
		int h = extenty;
		DataType datatype = sequence.getDataType_();
		sequence.beginUpdate();
		sequence.removeAllImages();
		try {
			switch(datatype) {
				case UBYTE:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c = 0; c < nbc; c++) {
								vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
								final byte[] inData = ((vtkUnsignedCharArray) myvtkarray).GetJavaArray();
								byte[] outData = new byte[w * h];
								for (int i = 0; i < h; i++) {
									for (int j = 0; j < w; j++) {
										outData[i * w + j] =  inData[z * w * h + i * w + j];
									}
								}
								image.setDataXYAsByte(c, outData);
							}
							sequence.setImage(t, z, image);
						}
					}
					break;
				case BYTE:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c=0; c < nbc; c++){
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
							sequence.setImage(t, z, image);
						}
					}
					break;
				case USHORT:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c=0; c < nbc; c++){
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
							sequence.setImage(t, z, image);
						}
					}
					break;
				case SHORT:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c=0; c < nbc; c++){
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
							sequence.setImage(t, z, image);
						}
					}
					break;
				case INT:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c=0; c < nbc; c++){
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
							sequence.setImage(t, z, image);
						}
					}
					break;
				case UINT:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c=0; c < nbc; c++){
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
							sequence.setImage(t, z, image);
						}
					}
					break;
				case FLOAT:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c=0; c < nbc; c++){
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
							sequence.setImage(t, z, image);
						}
					}
					break;
				case DOUBLE:
					for (int t = 0; t < nbt; t++) {
						for (int z = 0; z < nbz; z++) {
							IcyBufferedImage image = new IcyBufferedImage(w, h, nbc, datatype);
							progress.setPosition(z);
							for (int c=0; c < nbc; c++){
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
							sequence.setImage(t, z, image);
						}
					}
					break;
				default:
					break;
			}
		
			sequence.setPixelSizeX(spacingx);
			sequence.setPixelSizeY(spacingy);
			sequence.setPixelSizeZ(spacingz);
		} finally {
			sequence.endUpdate();
		}

		progress.close();
		System.out.println("have been applied");
		roiUpdater.updateRoi(sourceTransformedDataset, sequence);
	}

	private void converttoVtkImageData(int posC) {
		final Sequence sequence2 = sequence;
		if (sequence == null)
			return;

		final int sizeX = sequence2.getSizeX();
		final int sizeY = sequence2.getSizeY();
		final int sizeZ = sequence2.getSizeZ();
		final DataType dataType = sequence2.getDataType_();
		final int posT = 0;

		final vtkImageData newImageData = new vtkImageData();
		newImageData.SetDimensions(sizeX, sizeY, sizeZ);
		newImageData.SetSpacing(InputSpacingx, InputSpacingy, InputSpacingz);

		vtkDataArray array;

		switch (dataType) {
		case UBYTE:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkUnsignedCharArray) array).SetJavaArray(sequence.getDataCopyCXYZAsByte(posT));
			else
				((vtkUnsignedCharArray) array).SetJavaArray(sequence.getDataCopyXYZAsByte(posT, posC));
			break;

		case BYTE:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkUnsignedCharArray) array).SetJavaArray(sequence.getDataCopyCXYZAsByte(posT));
			else
				((vtkUnsignedCharArray) array).SetJavaArray(sequence.getDataCopyXYZAsByte(posT, posC));
			break;

		case USHORT:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_SHORT, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkUnsignedShortArray) array).SetJavaArray(sequence.getDataCopyCXYZAsShort(posT));
			else
				((vtkUnsignedShortArray) array).SetJavaArray(sequence.getDataCopyXYZAsShort(posT, posC));
			break;

		case SHORT:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_SHORT, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkShortArray) array).SetJavaArray(sequence.getDataCopyCXYZAsShort(posT));
			else
				((vtkShortArray) array).SetJavaArray(sequence.getDataCopyXYZAsShort(posT, posC));
			break;

		case UINT:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_INT, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkUnsignedIntArray) array).SetJavaArray(sequence.getDataCopyCXYZAsInt(posT));
			else
				((vtkUnsignedIntArray) array).SetJavaArray(sequence.getDataCopyXYZAsInt(posT, posC));
			break;

		case INT:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_INT, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkIntArray) array).SetJavaArray(sequence.getDataCopyCXYZAsInt(posT));
			else
				((vtkIntArray) array).SetJavaArray(sequence.getDataCopyXYZAsInt(posT, posC));
			break;

		case FLOAT:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_FLOAT, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkFloatArray) array).SetJavaArray(sequence.getDataCopyCXYZAsFloat(posT));
			else
				((vtkFloatArray) array).SetJavaArray(sequence.getDataCopyXYZAsFloat(posT, posC));
			break;

		case DOUBLE:
			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_DOUBLE, 1);
			array = newImageData.GetPointData().GetScalars();
			if (posC == -1)
				((vtkDoubleArray) array).SetJavaArray(sequence.getDataCopyCXYZAsDouble(posT));
			else
				((vtkDoubleArray) array).SetJavaArray(sequence.getDataCopyXYZAsDouble(posT, posC));
			break;

		default:
			newImageData.SetDimensions(1, 1, 1);
			newImageData.SetSpacing(sequence.getPixelSizeX(), sequence.getPixelSizeY(), sequence.getPixelSizeZ());
			newImageData.SetNumberOfScalarComponents(1, null);
			newImageData.SetExtent(0, 0, 0, 0, 0, 0);
			newImageData.AllocateScalars(null);
			break;
		}

		if (imageData[posC] != null) {
			final vtkPointData pointData = imageData[posC].GetPointData();
			if (pointData != null) {
				final vtkDataArray dataArray = pointData.GetScalars();
				if (dataArray != null)
					dataArray.Delete();
				pointData.Delete();
				imageData[posC].ReleaseData();
				imageData[posC].Delete();
			}
		}

		imageData[posC] = newImageData;
	}
}
