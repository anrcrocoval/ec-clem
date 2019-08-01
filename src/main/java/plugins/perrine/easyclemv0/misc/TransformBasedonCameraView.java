///**
// * Copyright 2010-2017 Perrine Paul-Gilloteaux, CNRS.
// * Perrine.Paul-Gilloteaux@univ-nantes.fr
// *
// * This file is part of EC-CLEM.
// *
// * you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// **/
//package plugins.perrine.easyclemv0;
//
//
//import icy.canvas.IcyCanvas;
//import icy.gui.dialog.MessageDialog;
//import icy.gui.frame.progress.ProgressFrame;
//import icy.gui.viewer.Viewer;
//import icy.image.IcyBufferedImage;
//import icy.sequence.Sequence;
//import icy.system.thread.ThreadUtil;
//import icy.type.DataType;
//import org.w3c.dom.Document;
//import plugins.adufour.ezplug.EzLabel;
//import plugins.adufour.ezplug.EzPlug;
//import plugins.adufour.ezplug.EzVarSequence;
//import plugins.adufour.ezplug.EzVarText;
//import plugins.kernel.canvas.VtkCanvas;
//import plugins.perrine.easyclemv0.storage.xml.*;
//import plugins.perrine.easyclemv0.storage.xml.rigid.RigidTransformationXmlWriter;
//import plugins.perrine.easyclemv0.matrix.MatrixUtil;
//import vtk.vtkCamera;
//import vtk.vtkDataArray;
//import vtk.vtkDataSet;
//import vtk.vtkDoubleArray;
//import vtk.vtkFloatArray;
//import vtk.vtkImageChangeInformation;
//import vtk.vtkImageData;
//import vtk.vtkImageReslice;
//import vtk.vtkIntArray;
//import vtk.vtkMatrix4x4;
//import vtk.vtkPointData;
//import vtk.vtkPoints;
//import vtk.vtkPolyData;
//import vtk.vtkShortArray;
//import vtk.vtkTransform;
//import vtk.vtkTransformPolyDataFilter;
//import vtk.vtkUnsignedCharArray;
//import vtk.vtkUnsignedIntArray;
//import vtk.vtkUnsignedShortArray;
//
//import java.io.File;
//
//public class TransformBasedonCameraView extends EzPlug {
//
//	private EzVarSequence source;
//	private Sequence sequence;
//	private vtkDataSet[] imageData;
//	private double InputSpacingx;
//	private double InputSpacingy;
//	private double InputSpacingz;
//	private Runnable transformer;
//	private Sequence sequence2;
//
//	private static String INPUT_SELECTION_CROP = "crop the data to match original dimensions (keep size and metadata)";
//	private static String INPUT_SELECTION_KEEP = "keep full output volume (adapt image size but keep metadata";
//
//	private EzVarText choiceinputsection = new EzVarText(
//		"Output volume:",
//		new String[]{
//			INPUT_SELECTION_CROP,
//			INPUT_SELECTION_KEEP
//		},0, false);
//	private boolean modeboundingbox;
//
//	@Override
//	public void clean() {
//	}
//
//	@Override
//	protected void execute() {
//		sequence = source.getValue();
//
//		if(sequence == null) {
//			MessageDialog.showDialog("Source was closed. Please open one and try again");
//			return;
//		}
//
//		if (choiceinputsection.getValue().equals(INPUT_SELECTION_CROP)) {
//			modeboundingbox = false;
//		} else {
//			modeboundingbox = true;
//		}
//
//		Viewer v=sequence.getFirstViewer();
//		IcyCanvas mycanvas = v.getCanvas();
//
//		if (v.getCanvas().getClass().getName().equals("plugins.kernel.canvas.VtkCanvas")) {
//			VtkCanvas test=(VtkCanvas) mycanvas;
//			vtkCamera mycam=test.getCamera();
//			System.out.println("View plane normal: "+mycam.GetViewPlaneNormal()[0]);
//			System.out.println(mycam.GetViewPlaneNormal()[1]);
//			System.out.println(mycam.GetViewPlaneNormal()[2]);
//			vtkMatrix4x4 viewmatrix=mycam.GetViewTransformMatrix();
//			vtkMatrix4x4 newmatrix=new vtkMatrix4x4();
//			for (int i=0;i<4;i++) {
//				for (int j=0;j<4;j++) {
//					newmatrix.SetElement(i, j, viewmatrix.GetElement(i, j));
//				}
//			}
//			newmatrix.SetElement(0, 3, 0);
//			newmatrix.SetElement(1, 3,0);
//			newmatrix.SetElement(2, 3,0);
//			vtkMatrix4x4 correctionMatrix=new vtkMatrix4x4();
//			correctionMatrix.Identity();
//			correctionMatrix.SetElement(1, 1,-1);
//			correctionMatrix.SetElement(2, 2,-1);
//			final vtkTransform viewtransform=new vtkTransform();
//			viewtransform.SetMatrix(newmatrix);
//			viewtransform.PostMultiply();
//			viewtransform.Concatenate(correctionMatrix);
//			this.InputSpacingx=this.sequence.getPixelSizeX();
//			this.InputSpacingy=this.sequence.getPixelSizeY();
//			this.InputSpacingz=this.sequence.getPixelSizeZ();
//			final DataType oriType = sequence.getDataType_();
//
//			transformer = new Runnable() {
//				@Override
//				public void run() {
//					System.out.println("I will apply transfo now");
//					ProgressFrame progress = new ProgressFrame("Applying the transformation...");
//					int nbc = sequence.getSizeC();
//					imageData=new vtkDataSet[nbc];
//
//					String name = source.getValue().getFilename() + "_3D_MANUAL_ROTATE_transfo.xml";
//					File XMLFile = new File(name);
//					XmlFileReader xmlFileReader = new XmlFileReader();
//					Document document = xmlFileReader.loadFile(XMLFile);
//					XmlFileWriter xmlFileWriter = new XmlFileWriter(document, XMLFile);
//					XmlTransformationWriter xmlWriter = new XmlTransformationWriter(document);
//					SequenceSizeXmlWriter sequenceSizeXmlWriter = new SequenceSizeXmlWriter(document);
//					sequenceSizeXmlWriter.writeSizeOf(sequence);
//					xmlWriter.write(MatrixUtil.convert(viewtransform.GetMatrix()), 0);
//
//					int nbt = sequence.getSizeT();
//					int nbz = sequence.getSizeZ();
//					int w = sequence.getSizeX();
//					int h = sequence.getSizeY();
//					for (int c=0;c<sequence.getSizeC();c++) {
//
//						converttoVtkImageData(c);
//						vtkImageChangeInformation change=new vtkImageChangeInformation();
//						change.SetInputData(imageData[c]);
//						change.CenterImageOn();
//						change.Update();
//						vtkImageReslice ImageReslice = new vtkImageReslice();
//						ImageReslice.SetInputData(change.GetOutput());
//						ImageReslice.SetOutputDimensionality(3);
//						ImageReslice.SetOutputSpacing(InputSpacingx, InputSpacingy, InputSpacingz);
//
//						if (modeboundingbox) {
//							vtkTransformPolyDataFilter tr=new  vtkTransformPolyDataFilter();
//							vtkPoints mypoints = new vtkPoints();
//							mypoints.SetNumberOfPoints(2);
//							mypoints.SetPoint(0,0, 0,0);
//							mypoints.SetPoint(1,sequence.getSizeX()*InputSpacingx, sequence.getSizeY()*InputSpacingy,sequence.getSizeZ()*InputSpacingz);
//							vtkPolyData boundspolydata=new vtkPolyData();
//							boundspolydata.SetPoints(mypoints);
//							tr.SetInputData(boundspolydata);
//							tr.SetTransform(viewtransform);
//							tr.Update();
//							vtkPolyData modifiedboundpoints = tr.GetOutput();
//							double [] bounds= new double[6];
//							double[] newpos=modifiedboundpoints.GetPoint(0);
//							bounds[0]=Math.abs(newpos[0]/InputSpacingx);
//							bounds[2]=Math.abs(newpos[1]/InputSpacingy);
//							bounds[4]=Math.abs(newpos[2]/InputSpacingz);
//							newpos=modifiedboundpoints.GetPoint(1);
//							bounds[1]=Math.abs(newpos[0]/InputSpacingx);
//							bounds[3]=Math.abs(newpos[1]/InputSpacingy);
//							bounds[5]=Math.abs(newpos[2]/InputSpacingz);
//							w = 1+(int)bounds[1]-(int)bounds[0];
//							h = 1+(int)bounds[3]-(int)bounds[2];
//							nbz=1+(int)bounds[5]-(int)bounds[4];
//							ImageReslice.SetOutputExtent((int)bounds[0],(int)bounds[1],(int)bounds[2],(int)bounds[3],(int)bounds[4],(int)bounds[5]);
//						} else {
//							ImageReslice.SetOutputExtent(0, sequence.getSizeX()-1, 0, sequence.getSizeY()-1, 0, sequence.getSizeZ()-1); // to be checked: transform is applied twice?
//							nbt = sequence.getSizeT();
//							nbz = sequence.getSizeZ();
//							w = sequence.getSizeX();
//							h = sequence.getSizeY();
//						}
//						ImageReslice.SetResliceTransform(viewtransform.GetInverse());
//						System.out.println(viewtransform.GetInverse());
//						ImageReslice.SetInterpolationModeToLinear();
//						ImageReslice.Update();
//						imageData[c] = ImageReslice.GetOutput();
//					}
//
//					DataType datatype = sequence.getDataType_();
//					sequence2.beginUpdate();
//					sequence2.removeAllImages();
//					progress.setLength(nbz);
//					try {
//						switch(oriType) {
//							case UBYTE:
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final byte[] inData=((vtkUnsignedCharArray) myvtkarray).GetJavaArray();
//											byte[] outData=new byte[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsByte(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							case BYTE:
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final byte[] inData=((vtkUnsignedCharArray) myvtkarray).GetJavaArray();
//											byte[] outData=new byte[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsByte(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							case USHORT:
//
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final short[] inData=((vtkUnsignedShortArray) myvtkarray).GetJavaArray();
//											short[] outData=new short[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsShort(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							case SHORT:
//
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final short[] inData=((vtkShortArray) myvtkarray).GetJavaArray();
//											short[] outData=new short[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsShort(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							case INT:
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final int[] inData=((vtkIntArray) myvtkarray).GetJavaArray();
//											int[] outData=new int[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsInt(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							case UINT:
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final int[] inData=((vtkUnsignedIntArray) myvtkarray).GetJavaArray();
//											int[] outData=new int[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsInt(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							case FLOAT:
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final float[] inData=((vtkFloatArray) myvtkarray).GetJavaArray();
//											float[] outData=new float[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsFloat(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							case DOUBLE:
//								for (int t = 0; t < nbt; t++) {
//									for (int z = 0; z < nbz; z++) {
//										IcyBufferedImage image = new IcyBufferedImage(w, h, nbc,
//												datatype);
//										progress.setPosition(z);
//										for (int c=0;c<nbc;c++){
//											vtkDataArray myvtkarray = imageData[c].GetPointData().GetScalars();
//											final double[] inData=((vtkDoubleArray) myvtkarray).GetJavaArray();
//											double[] outData=new double[w*h];
//											for (int i = 0; i < h; i++) {
//												for (int j = 0; j < w; j++) {
//													outData[i * w + j] =  inData[z * w * h + i * w + j];
//												}
//											}
//											image.setDataXYAsDouble(c, outData);
//										}
//										sequence2.setImage(t, z, image);
//									}
//								}
//								break;
//							default:
//								break;
//						}
//						sequence2.setPixelSizeX(InputSpacingx);
//						sequence2.setPixelSizeY(InputSpacingy);
//						sequence2.setPixelSizeZ(InputSpacingz);
//					} finally {
//						sequence2.endUpdate();
//					}
//					progress.close();
//					sequence2.setName("rotated");
//					System.out.println("have been applied");
//				}
//			};
//			ThreadUtil.bgRunSingle(transformer);
//			addSequence(sequence2);
//		} else {
//			MessageDialog.showDialog("Please switch to 3D view");
//		}
//	}
//
//	@Override
//	protected void initialize() {
//		EzLabel textinfo1=new EzLabel("Manual Prealignment:");
//		EzLabel textinfo3=new EzLabel("Please select the stack you want to transform,\n select 3D view canvas and then,\n turn in the direction you want to reslice it. \nWhen ready, press play.The transform will be saved \n and can be reapplied through Ec-CLEM later if needed. ");
//		source = new EzVarSequence("Select Source Stack ");
//		addEzComponent(textinfo1);
//		addEzComponent(source);
//		addEzComponent(textinfo3);
//		addEzComponent(choiceinputsection);
//	}
//
//	/**
//	 * this part is a copy and paste from canvas3D Icy
//	 * there is a big limitation for now: it will apply only on one channel, one time frame
//	 */
//	private void converttoVtkImageData(int posC) {
//
//		if (this.sequence == null)
//			return;
//
//		final int sizeX = sequence.getSizeX();
//		final int sizeY = sequence.getSizeY();
//		final int sizeZ = sequence.getSizeZ();
//		final DataType dataType = sequence.getDataType_();
//		final int posT = sequence.getFirstViewer().getPositionT();
//		final vtkImageData newImageData = new vtkImageData();
//		newImageData.SetDimensions(sizeX, sizeY, sizeZ);
//		newImageData.SetSpacing(this.InputSpacingx, this.InputSpacingy, this.InputSpacingz);
//		vtkDataArray array;
//		switch (dataType) {
//		case UBYTE:
//			// newImageData.SetScalarTypeToUnsignedChar();
//			// pre-allocate data
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkUnsignedCharArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsByte(posT));
//			else
//				((vtkUnsignedCharArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsByte(posT, posC));
//			break;
//
//		case BYTE:
//
//			// newImageData.SetScalarTypeToUnsignedChar();
//			// pre-allocate data
//			// newImageData.AllocateScalars();
//			// pre-allocate data
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkUnsignedCharArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsByte(posT));
//			else
//				((vtkUnsignedCharArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsByte(posT, posC));
//			break;
//
//		case USHORT:
//			// newImageData.SetScalarTypeToUnsignedShort();
//			// pre-allocate data
//			// newImageData.AllocateScalars();
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_SHORT, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkUnsignedShortArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsShort(posT));
//			else
//				((vtkUnsignedShortArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsShort(posT, posC));
//			break;
//
//		case SHORT:
//			// newImageData.SetScalarTypeToShort();
//			// pre-allocate data
//			// newImageData.AllocateScalars();
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_SHORT, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkShortArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsShort(posT));
//			else
//				((vtkShortArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsShort(posT, posC));
//			break;
//
//		case UINT:
//			// newImageData.SetScalarTypeToUnsignedInt();
//			// pre-allocate data
//			// newImageData.AllocateScalars();
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_INT, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkUnsignedIntArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsInt(posT));
//			else
//				((vtkUnsignedIntArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsInt(posT, posC));
//			break;
//
//		case INT:
//			// newImageData.SetScalarTypeToInt();
//			// pre-allocate data
//			// newImageData.AllocateScalars();
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_INT, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkIntArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsInt(posT));
//			else
//				((vtkIntArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsInt(posT, posC));
//			break;
//
//		case FLOAT:
//			// newImageData.SetScalarTypeToFloat();
//			// pre-allocate data
//			// newImageData.AllocateScalars();
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_FLOAT, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkFloatArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsFloat(posT));
//			else
//				((vtkFloatArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsFloat(posT, posC));
//			break;
//
//		case DOUBLE:
//			// newImageData.SetScalarTypeToDouble();
//			// pre-allocate data
//			// newImageData.AllocateScalars();
//			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_DOUBLE, 1);
//			// get array structure
//			array = newImageData.GetPointData().GetScalars();
//			// set frame sequence data in the array structure
//			if (posC == -1)
//				((vtkDoubleArray) array).SetJavaArray(sequence
//						.getDataCopyCXYZAsDouble(posT));
//			else
//				((vtkDoubleArray) array).SetJavaArray(sequence
//						.getDataCopyXYZAsDouble(posT, posC));
//			break;
//
//		default:
//			// we probably have an empty sequence
//			newImageData.SetDimensions(1, 1, 1);
//			newImageData.SetSpacing(sequence.getPixelSizeX(), sequence.getPixelSizeY(), sequence.getPixelSizeZ());
//			newImageData.SetNumberOfScalarComponents(1, null);
//			newImageData.SetExtent(0, 0, 0, 0, 0, 0);
//			// newImageData.SetScalarTypeToUnsignedChar();
//			// pre-allocate data
//			newImageData.AllocateScalars(null);
//			break;
//		}
//
//		// set connection
//		// volumeMapper.SetInput(newImageData);
//		// mark volume as modified
//		// volume.Modified();
//
//		// release previous volume data memory
//		if (imageData[posC] != null) {
//			final vtkPointData pointData = imageData[posC].GetPointData();
//			if (pointData != null) {
//				final vtkDataArray dataArray = pointData.GetScalars();
//				if (dataArray != null)
//					dataArray.Delete();
//				pointData.Delete();
//				imageData[posC].ReleaseData();
//				imageData[posC].Delete();
//			}
//		}
//
//		imageData[posC] = newImageData;
//	}
//}
