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

import icy.gui.viewer.Viewer;
import icy.image.colormap.IcyColorMap;
import icy.image.colormodel.IcyColorModel;
import icy.image.colormodel.IcyColorModelEvent;
import icy.image.lut.LUT;
import icy.main.Icy;
import icy.sequence.SequenceUtil;
import icy.type.DataType;
import icy.vtk.VtkUtil;
import plugins.perrine.ec_clem.ec_clem.progress.ProgressTrackable;
import plugins.perrine.ec_clem.ec_clem.progress.ProgressTrackableMasterTask;

import plugins.perrine.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.perrine.ec_clem.ec_clem.transformation.Transformation;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;

import vtk.vtkAbstractTransform;
import vtk.vtkImageData;
import vtk.vtkImageReslice;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;


public class Stack3DVTKTransformer extends ProgressTrackableMasterTask implements Supplier<Sequence> {

	private vtkImageReslice imageReslice;
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

	private DataType dataType;
	private int sizeT;
	private int sizeC;

	private VtkAbstractTransformFactory vtkAbstractTransformFactory;

	public Stack3DVTKTransformer(Sequence sequence, SequenceSize sequenceSize, Transformation transformation) {
		DaggerStack3DVTKTransformerComponent.builder().build().inject(this);
		setSourceSequence(sequence);
		setTargetSize(sequenceSize);
		this.transformation = transformation;
		this.dataType = sequence.getDataType_();
		this.sizeT = sequence.getSizeT();
		this.sizeC = sequence.getSizeC();
		for(int i = 0; i < sequence.getSizeC(); i++) {
			super.add(
				new VtkDataSequenceSupplier(
					sequence,
					dataType,
					i,
					sizeC,
					null,
					extentx, extenty, extentz, sizeT, spacingx, spacingy, spacingz
				)
			);
		}
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
		imageReslice.ReleaseDataFlagOn();

		List<LUT> lutList = new ArrayList<>();
		List<Viewer> viewers = sequence.getViewers();
		for(Viewer viewer : viewers) {
			viewer.refreshCanvasCombo();
			lutList.add(viewer.getLut());
		}

		sequence.beginUpdate();
		List<Sequence> channels = new LinkedList<>();
		for(int c = 0; c < sequence.getSizeC(); c++) {
			try {
				channels.add(SequenceUtil.extractChannel(sequence, c));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sequence.removeAllImages();
		List<ProgressTrackable> taskList = super.getTaskList();
		for (int c = 0; c < sizeC; c++) {
			vtkImageData vtkImageData = converttoVtkImageData(channels.remove(0));
			imageReslice.SetInputData(vtkImageData);
			imageReslice.Modified();
			imageReslice.Update();
			VtkDataSequenceSupplier progressTrackable = (VtkDataSequenceSupplier) taskList.get(c);
			progressTrackable.setData(VtkUtil.getJavaArray(imageReslice.GetOutput().GetPointData().GetScalars()));
			progressTrackable.get();
			vtkImageData.ReleaseData();
			vtkImageData.Delete();
		}
		imageReslice.Delete();
		VtkUtil.vtkGC();
		for(int i = 0; i < viewers.size(); i++) {
			viewers.get(i).setLut(lutList.get(i));
			sequence.setAutoUpdateChannelBounds(true);
		}
		sequence.endUpdate();
		return sequence;
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

	private vtkImageData converttoVtkImageData(Sequence singleChannelSequence) {
		vtkImageData newImageData = new vtkImageData();
		newImageData.SetDimensions(singleChannelSequence.getSizeX(), singleChannelSequence.getSizeY(), singleChannelSequence.getSizeZ());
		newImageData.SetSpacing(InputSpacingx, InputSpacingy, InputSpacingz);
		Object dataCopyXYZT = singleChannelSequence.getDataCopyXYZT(0);
		newImageData.GetPointData().SetScalars(VtkUtil.getVtkArray(dataCopyXYZT, true));
		return newImageData;
	}

	@Inject
	public void setVtkAbstractTransformFactory(VtkAbstractTransformFactory vtkAbstractTransformFactory) {
		this.vtkAbstractTransformFactory = vtkAbstractTransformFactory;
	}
}
