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

import icy.vtk.VtkUtil;
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import icy.type.DataType;
import plugins.fr.univ_nantes.ec_clem.progress.ProgressTrackableMasterTask;
import vtk.*;
import javax.inject.Inject;
import java.lang.reflect.Array;
import java.util.function.Supplier;

/**
 * The difference with 2D transform is that the tranform is computed in REAL UNITS, because vtk apply it in real unit,
 * which can be quite convenient for dealing with anisotropy!
 */
public class Stack3DVTKTransformer extends ProgressTrackableMasterTask implements Supplier<Sequence> {

	private vtkImageReslice imageReslice;
	private Object[] imageData;

	private Sequence sequence;
	private Transformation transformation;
	private int extentx;
	private int extenty;
	private int extentz;
	private double spacingx;
	private double spacingy;
	private double spacingz;
	private double InputSpacingz;
	private double InputSpacingx;
	private double InputSpacingy;

	private VtkAbstractTransformFactory vtkAbstractTransformFactory;
	private VtkDataSequenceSupplier vtkDataSequenceSupplier;

	public Stack3DVTKTransformer(Sequence sequence, SequenceSize sequenceSize, Transformation transformation) {
		DaggerStack3DVTKTransformerComponent.builder().build().inject(this);
		setSourceSequence(sequence);
		setTargetSize(sequenceSize);
		this.transformation = transformation;
		imageData = new Object[sequence.getSizeC()];
	}

	@Override
	public Sequence get() {
		vtkAbstractTransform mytransfo = vtkAbstractTransformFactory.getFrom(transformation);
		imageReslice = new vtkImageReslice();
		imageReslice.SetOutputDimensionality(3);
		imageReslice.SetOutputOrigin(0, 0, 0);
		imageReslice.SetOutputSpacing(spacingx, spacingy, spacingz);
		imageReslice.SetOutputExtent(0, extentx - 1, 0, extenty - 1, 0, extentz - 1);
		imageReslice.SetResliceTransform(mytransfo);
		imageReslice.SetInterpolationModeToLinear();

		for (int c = 0; c < sequence.getSizeC(); c++) {
			vtkImageData vtkImageData = converttoVtkImageData(c);
			imageReslice.SetInputData(vtkImageData);
			imageReslice.Modified();
			imageReslice.Update();
			imageData[c] = VtkUtil.getJavaArray(imageReslice.GetOutput().GetPointData().GetScalars());
		}
		vtkDataSequenceSupplier = new VtkDataSequenceSupplier(sequence, imageData, extentx, extenty, extentz, this.sequence.getSizeT(), spacingx, spacingy, spacingz);
		super.add(vtkDataSequenceSupplier);
		return vtkDataSequenceSupplier.get();
	}

	private void setSourceSequence(Sequence sequence) {
		this.sequence = sequence;
		InputSpacingx = sequence.getPixelSizeX();
		InputSpacingy = sequence.getPixelSizeY();
		InputSpacingz = sequence.getPixelSizeZ();
	}

	private void setTargetSize(SequenceSize sequenceSize) {
		this.extentx = sequenceSize.get(DimensionId.X).getSize();
		this.extenty = sequenceSize.get(DimensionId.Y).getSize();
		this.extentz = sequenceSize.get(DimensionId.Z).getSize();
		this.spacingx = sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer();
		this.spacingy = sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer();
		this.spacingz = sequenceSize.get(DimensionId.Z).getPixelSizeInMicrometer();
	}

	private vtkImageData converttoVtkImageData(int posC) {

		int sizeX = sequence.getSizeX();
		int sizeY = sequence.getSizeY();
		int sizeZ = sequence.getSizeZ();
		DataType dataType = sequence.getDataType_();

		vtkImageData image = new vtkImageData();
		image.SetDimensions(sizeX, sizeY, sizeZ);
  		image.SetOrigin(0.0, 0.0, 0.0);
  		image.SetSpacing(InputSpacingx, InputSpacingy, InputSpacingz);
		image.AllocateScalars(VtkUtil.getVtkType(dataType), 1);
  		vtkDataArray array = image.GetPointData().GetScalars();

		Object input = sequence.getDataXYZT(posC);
		for(int t = 0; t < sequence.getSizeT(); t++){
			for(int z= 0; z < sequence.getSizeZ(); z++){
				for(int xy = 0; xy < sequence.getSizeX() * sequence.getSizeY(); xy++){
					array.InsertNextTuple1(Array.getDouble(Array.get(Array.get(input, t), z), xy));
				}
			}
		}

//		vtkImageData newImageData = new vtkImageData();
//		newImageData.SetDimensions(sizeX, sizeY, sizeZ);
//		newImageData.SetSpacing(InputSpacingx, InputSpacingy, InputSpacingz);
//		vtkDataArray array;
//		switch (dataType) {
//			case UBYTE:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkUnsignedCharArray) array).SetJavaArray(sequence.getDataCopyXYZTAsByte(posC));
//				break;
//
//			case BYTE:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkUnsignedCharArray) array).SetJavaArray(sequence.getDataCopyXYZTAsByte(posC));
//				break;
//
//			case USHORT:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_SHORT, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkUnsignedShortArray) array).SetJavaArray(sequence.getDataCopyXYZTAsShort(posC));
//				break;
//
//			case SHORT:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_SHORT, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkShortArray) array).SetJavaArray(sequence.getDataCopyXYZTAsShort(posC));
//				break;
//
//			case UINT:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_INT, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkUnsignedIntArray) array).SetJavaArray(sequence.getDataCopyXYZTAsInt(posC));
//				break;
//
//			case INT:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_INT, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkIntArray) array).SetJavaArray(sequence.getDataCopyXYZTAsInt(posC));
//				break;
//
//			case FLOAT:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_FLOAT, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkFloatArray) array).SetJavaArray(sequence.getDataCopyXYZTAsFloat(posC));
//				break;
//
//			case DOUBLE:
//				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_DOUBLE, 1);
//				array = newImageData.GetPointData().GetScalars();
//				((vtkDoubleArray) array).SetJavaArray(sequence.getDataCopyXYZTAsDouble(posC));
//				break;
//
//			default:
//				throw new RuntimeException("Unsupported type");
//		}

		return image;
	}

	@Inject
	public void setVtkAbstractTransformFactory(VtkAbstractTransformFactory vtkAbstractTransformFactory) {
		this.vtkAbstractTransformFactory = vtkAbstractTransformFactory;
	}
}
