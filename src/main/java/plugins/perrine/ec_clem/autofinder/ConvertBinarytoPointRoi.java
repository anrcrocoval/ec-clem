package plugins.perrine.ec_clem.autofinder;

import java.awt.Color;
import java.util.ArrayList;

import icy.image.IcyBufferedImage;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceDataIterator;
import icy.type.DataIteratorUtil;
import icy.type.DataType;
import icy.type.point.Point5D;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;
//import plugins.adufour.ezplug.EzVarInteger;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.kernel.roi.roi3d.ROI3DPoint;
//import vtk.vtkContourTriangulator;
import vtk.vtkDataArray;

import vtk.vtkDecimatePro;
import vtk.vtkDoubleArray;
import vtk.vtkFloatArray;
import vtk.vtkImageData;
import vtk.vtkImageResample;
import vtk.vtkIntArray;
import vtk.vtkMarchingCubes;
import vtk.vtkMarchingSquares;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkShortArray;
//import vtk.vtkTriangleFilter;
import vtk.vtkUnsignedCharArray;
import vtk.vtkUnsignedIntArray;
import vtk.vtkUnsignedShortArray;


public class ConvertBinarytoPointRoi extends EzPlug implements Block{
	private EzVarSequence source=new EzVarSequence("Binary Image");
	//private EzVarInteger downsampling=new EzVarInteger("Downsampling for Roi generation",1,1,100,2 );
	private EzVarDouble newsize=new EzVarDouble("put points every (in um)",1,0.01,100,0.1 );
	private EzVarBoolean fromrois=new EzVarBoolean("Generate point from Rois", false);
	
	
	
	private vtkImageData imageData;
	
	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		imageData=converttoVtkImageData(0, source.getValue(),false) ;
		double resampling=newsize.getValue(); //in um
		double reductionfactorx= source.getValue().getPixelSizeX()/resampling;
		double reductionfactorz= source.getValue().getPixelSizeZ()/resampling;
		vtkImageResample resampler=new vtkImageResample();
		resampler.SetInputData(imageData);
		 resampler.SetAxisMagnificationFactor(0, reductionfactorx);
		 resampler.SetAxisMagnificationFactor(1, reductionfactorx);
		 resampler.SetAxisMagnificationFactor(2, reductionfactorz);
		 resampler.Update();
		int downsample=1;
		System.out.println(source.getValue().getChannelMax(0));
		// extract the surface by MarchingCubes
		double contourlevel=255;
		if (!fromrois.getValue())
			contourlevel=source.getValue().getChannelMax(0);
		if (source.getValue().getSizeZ()==1){
			vtkMarchingSquares surfaceextractor2D=new vtkMarchingSquares();
			surfaceextractor2D.SetInputData(resampler.GetOutput());
			surfaceextractor2D.SetValue(0,contourlevel); // binary?
			//surfaceextractor.ComputeGradientsOn();
			
			
			surfaceextractor2D.Update();
			/*vtkDecimatePro deci = new vtkDecimatePro();
			vtkContourTriangulator triangles= new vtkContourTriangulator();
			triangles.SetInputData(surfaceextractor2D.GetOutput());
			
			System.out.println(surfaceextractor2D.GetOutput().GetNumberOfPoints());
			System.out.println(surfaceextractor2D.GetOutput().GetNumberOfPolys());
			System.out.println(surfaceextractor2D.GetOutput().GetNumberOfLines());
			System.out.println(surfaceextractor2D.GetOutput().GetNumberOfStrips());
			triangles.Update();
			System.out.println(triangles.GetOutput().GetNumberOfPoints());
			System.out.println(triangles.GetOutput().GetNumberOfPolys());
			deci.SetInputData(triangles.GetOutput());
			deci.SetTargetReduction((double)downsampling.getValue()/100);
			deci.PreserveTopologyOn();
			deci.Update();*/
			vtkPolyData surface=surfaceextractor2D.GetOutput();
			vtkPoints extractedpoints=surface.GetPoints();
			if (surface.GetNumberOfPoints()==0)
				return;
			System.out.println(extractedpoints.GetNumberOfPoints());
			 CreateRoifromPoints(source.getValue(),surface,Color.GREEN, downsample);
		}
		else
		{
		vtkMarchingCubes surfaceextractor=new vtkMarchingCubes();
		surfaceextractor.SetInputData(resampler.GetOutput());
		surfaceextractor.SetValue(0,source.getValue().getChannelMax(0)); // binary?
		//surfaceextractor.ComputeGradientsOn();
		surfaceextractor.ComputeScalarsOn();
		surfaceextractor.ComputeNormalsOn();
		surfaceextractor.ComputeGradientsOn();
		surfaceextractor.Update();
		//vtkPolyData surface=surfaceextractor.GetOutput();
		vtkDecimatePro deci = new vtkDecimatePro();
		deci.SetInputConnection(surfaceextractor.GetOutputPort());
		deci.SetTargetReduction(0.9);
		//deci.PreserveTopologyOn();
		deci.Update();
		vtkPolyData surface=deci.GetOutput();
		vtkPoints extractedpoints=surface.GetPoints();
		System.out.println(extractedpoints.GetNumberOfPoints());
		 CreateRoifromPoints(source.getValue(),surface,Color.RED,downsample);
		}
	}
	protected void CreateRoifromPoints(Sequence seq, vtkPolyData points,Color mycolor, int downsample) {
		vtkPoints listofpoints = points.GetPoints();
		// seq.removeAllROI();
		for (int i = 0; i < points.GetNumberOfPoints(); i=i+downsample) {
			//for (int i = 0; i < 5; i++) {
			ROI3DPoint roi = new ROI3DPoint();

			Point5D position = roi.getPosition5D();
			position.setX(listofpoints.GetPoint(i)[0] / seq.getPixelSizeX());
			position.setY(listofpoints.GetPoint(i)[1] / seq.getPixelSizeY());
			position.setZ(listofpoints.GetPoint(i)[2] / seq.getPixelSizeZ());
			/*position.setX(listofpoints.GetPoint(i)[0] );
			position.setY(listofpoints.GetPoint(i)[1] );
			position.setZ(listofpoints.GetPoint(i)[2] );*/
			roi.setPosition5D(position);
			//roi.setZd(listofpoints.GetPoint(i)[2] / seq.getPixelSizeZ()); //not needed anymore, display dealt with by ROI3DPoint;
			// (was my own roi3d before
			roi.setName(" Point " + i);
			roi.setColor(mycolor);
			seq.addROI(roi);

		}
		return;
	}
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		addEzComponent(source);
		addEzComponent(newsize);
		addEzComponent(fromrois);
		//addEzComponent(downsampling);
	}
	vtkImageData converttoVtkImageData(int posC, Sequence seq,boolean affectfield) {
		final Sequence sequence2 = seq;
		final int sizeX = sequence2.getSizeX();
		final int sizeY = sequence2.getSizeY();
		final int sizeZ = sequence2.getSizeZ();
		final vtkImageData newImageData = new vtkImageData();
		if (seq == null)
			return null;
		//if (fromRoi)
		final Sequence out = new Sequence("ROI conversion");
		if (fromrois.getValue()){
			ArrayList<ROI> rois = sequence2.getROIs();



			out.beginUpdate();
			try
			{
				for (int t = 0; t < sequence2.getSizeT(); t++)
					for (int z = 0; z < sequence2.getSizeZ(); z++)
						out.setImage(t, z, new IcyBufferedImage(sequence2.getSizeX(), sequence2.getSizeY(), 1, DataType.UBYTE));

				// set value from ROI(s)
				for (ROI roi : rois)
					if (!roi.getBounds5D().isEmpty())
						try {
							DataIteratorUtil.set(new SequenceDataIterator(out, roi), 255);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

				// notify data changed
				out.dataChanged();
			}
			finally
			{
				out.endUpdate();
				sequence2.removeAllROI();
			}
			out.setPixelSizeX(seq.getPixelSizeX());
			out.setPixelSizeY(seq.getPixelSizeY());
			out.setPixelSizeZ(seq.getPixelSizeZ());
			//addSequence(out);



			final int posT;
			if (!this.isHeadLess()){
				posT = sequence2.getFirstViewer().getPositionT();
			}
			else{
				posT=0;
			}



			// create a new image data structure


			newImageData.SetDimensions(sizeX, sizeY, sizeZ);
			newImageData.SetSpacing(seq.getPixelSizeX(), seq.getPixelSizeY(), seq.getPixelSizeZ());

			vtkDataArray array;

			newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
			// get array structure
			array = newImageData.GetPointData().GetScalars();
			// set frame sequence data in the array structure
			if (posC == -1)
				((vtkUnsignedCharArray) array).SetJavaArray(out.getDataCopyCXYZAsByte(posT));
			else
				((vtkUnsignedCharArray) array).SetJavaArray(out.getDataCopyXYZAsByte(posT, posC));

		}
		else{
			final int posT;
			if (!this.isHeadLess()){
				posT = sequence2.getFirstViewer().getPositionT();
			}
			else{
				posT=0;
			}

			final DataType dataType =sequence2.getDataType_();
			// create a new image data structure


			newImageData.SetDimensions(sizeX, sizeY, sizeZ);
			newImageData.SetSpacing(seq.getPixelSizeX(), seq.getPixelSizeY(), seq.getPixelSizeZ());
			// all component ?
			// if (posC == -1)
			// newImageData.SetNumberOfScalarComponents(sequence.getSizeC(), null);
			// else
			// newImageData.SetNumberOfScalarComponents(1, null);
			// newImageData.SetExtent(0, sizeX - 1, 0, sizeY - 1, 0, sizeZ - 1);

			vtkDataArray array;

			switch (dataType) {
			case UBYTE:

				// newImageData.SetScalarTypeToUnsignedChar();
				// pre-allocate data
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkUnsignedCharArray) array).SetJavaArray(seq.getDataCopyCXYZAsByte(posT));
				else
					((vtkUnsignedCharArray) array).SetJavaArray(seq.getDataCopyXYZAsByte(posT, posC));
				break;

			case BYTE:

				// newImageData.SetScalarTypeToUnsignedChar();
				// pre-allocate data
				// newImageData.AllocateScalars();
				// pre-allocate data
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkUnsignedCharArray) array).SetJavaArray(seq.getDataCopyCXYZAsByte(posT));
				else
					((vtkUnsignedCharArray) array).SetJavaArray(seq.getDataCopyXYZAsByte(posT, posC));
				break;

			case USHORT:
				// newImageData.SetScalarTypeToUnsignedShort();
				// pre-allocate data
				// newImageData.AllocateScalars();
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_SHORT, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkUnsignedShortArray) array).SetJavaArray(seq.getDataCopyCXYZAsShort(posT));
				else
					((vtkUnsignedShortArray) array).SetJavaArray(seq.getDataCopyXYZAsShort(posT, posC));
				break;

			case SHORT:
				// newImageData.SetScalarTypeToShort();
				// pre-allocate data
				// newImageData.AllocateScalars();
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_SHORT, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkShortArray) array).SetJavaArray(seq.getDataCopyCXYZAsShort(posT));
				else
					((vtkShortArray) array).SetJavaArray(seq.getDataCopyXYZAsShort(posT, posC));
				break;

			case UINT:
				// newImageData.SetScalarTypeToUnsignedInt();
				// pre-allocate data
				// newImageData.AllocateScalars();
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_UNSIGNED_INT, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkUnsignedIntArray) array).SetJavaArray(seq.getDataCopyCXYZAsInt(posT));
				else
					((vtkUnsignedIntArray) array).SetJavaArray(seq.getDataCopyXYZAsInt(posT, posC));
				break;

			case INT:
				// newImageData.SetScalarTypeToInt();
				// pre-allocate data
				// newImageData.AllocateScalars();
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_INT, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkIntArray) array).SetJavaArray(seq.getDataCopyCXYZAsInt(posT));
				else
					((vtkIntArray) array).SetJavaArray(seq.getDataCopyXYZAsInt(posT, posC));
				break;

			case FLOAT:
				// newImageData.SetScalarTypeToFloat();
				// pre-allocate data
				// newImageData.AllocateScalars();
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_FLOAT, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkFloatArray) array).SetJavaArray(seq.getDataCopyCXYZAsFloat(posT));
				else
					((vtkFloatArray) array).SetJavaArray(seq.getDataCopyXYZAsFloat(posT, posC));
				break;

			case DOUBLE:
				// newImageData.SetScalarTypeToDouble();
				// pre-allocate data
				// newImageData.AllocateScalars();
				newImageData.AllocateScalars(icy.vtk.VtkUtil.VTK_DOUBLE, 1);
				// get array structure
				array = newImageData.GetPointData().GetScalars();
				// set frame sequence data in the array structure
				if (posC == -1)
					((vtkDoubleArray) array).SetJavaArray(seq.getDataCopyCXYZAsDouble(posT));
				else
					((vtkDoubleArray) array).SetJavaArray(seq.getDataCopyXYZAsDouble(posT, posC));
				break;

			default:
				// we probably have an empty sequence
				newImageData.SetDimensions(1, 1, 1);
				newImageData.SetSpacing(seq.getPixelSizeX(), seq.getPixelSizeY(),
						seq.getPixelSizeZ());
				newImageData.SetNumberOfScalarComponents(1, null);
				newImageData.SetExtent(0, 0, 0, 0, 0, 0);
				// newImageData.SetScalarTypeToUnsignedChar();
				// pre-allocate data
				newImageData.AllocateScalars(null);
				break;
			}

			// set connection
			// volumeMapper.SetInput(newImageData);
			// mark volume as modified
			// volume.Modified();

			// release previous volume data memory
		}

		return  newImageData;
	}

	@Override
	public void declareInput(VarList inputMap) {
		// TODO Auto-generated method stub
		inputMap.add("Sequence to process",source.getVariable());
		
		
		inputMap.add("sampling",newsize.getVariable());
		
		inputMap.add("read rois instead of binary",fromrois.getVariable());
	}

	@Override
	public void declareOutput(VarList outputMap) {
		// TODO Auto-generated method stub
		outputMap.add("sequence with Rois", source.getVariable());
	}

}
