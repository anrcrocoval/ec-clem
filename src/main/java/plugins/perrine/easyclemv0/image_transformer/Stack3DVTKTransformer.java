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
import icy.sequence.Sequence;
import icy.type.DataType;
import plugins.perrine.easyclemv0.factory.SequenceFactory;
import plugins.perrine.easyclemv0.factory.vtk.VtkAbstractTransformFactory;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.model.transformation.Transformation;
import vtk.*;

/**
 * The difference with 2D transform is that the tranform is computed in REAL UNITS, because vtk apply it in real unit,
 * which can be quite convenient for dealing with anisotropy!
 */
public class Stack3DVTKTransformer implements ImageTransformer {

	private vtkImageReslice imageReslice;
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

	private VtkAbstractTransformFactory vtkAbstractTransformFactory = new VtkAbstractTransformFactory();
	private SequenceFactory sequenceFactory = new SequenceFactory();

	public void setSourceSequence(Sequence sequence) {
		this.sequence = sequence;
		setSourceSize(sequence.getPixelSizeX(), sequence.getPixelSizeY(), sequence.getPixelSizeZ());
	}

	private void setSourceSize(double pixelSizeX, double pixelSizeY, double pixelSizeZ) {
		InputSpacingx = pixelSizeX;
		InputSpacingy = pixelSizeY;
		InputSpacingz = pixelSizeZ;
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

	public void run(Transformation transformation) {
		vtkAbstractTransform mytransfo = vtkAbstractTransformFactory.getFrom(transformation);
		imageReslice = new vtkImageReslice();
		imageReslice.SetOutputDimensionality(3);
		imageReslice.SetOutputOrigin(0, 0, 0);
		imageReslice.SetOutputSpacing(spacingx, spacingy, spacingz);
		imageReslice.SetOutputExtent(0, extentx - 1, 0, extenty - 1, 0, extentz - 1);
		imageReslice.SetResliceTransform(mytransfo.GetInverse());
		imageReslice.SetInterpolationModeToLinear();

		imageData = new vtkDataSet[sequence.getSizeC()];
		for (int c = 0; c < sequence.getSizeC(); c++) {
			converttoVtkImageData(c);
			imageReslice.SetInputData(imageData[c]);
			imageReslice.Update();
			imageData[c] = imageReslice.GetOutput();
		}

		sequence = sequenceFactory.getFrom(sequence, imageData, extentx, extenty, extentz, sequence.getSizeT(), spacingx, spacingy, spacingz);
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
