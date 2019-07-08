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


/**
 * 
 */
package plugins.perrine.easyclemv0;






import icy.common.listener.ProgressListener;
import icy.file.FileUtil;
import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.ProgressFrame;
import icy.gui.frame.progress.ToolTipFrame;
import icy.image.IcyBufferedImage;
import icy.imagej.ImageJUtil;
import icy.math.ArrayMath;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.type.collection.array.Array1DUtil;
import ij.IJ;
import ij.ImagePlus;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVar;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.adufour.ezplug.EzVarListener;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.ezplug.EzVarText;
import plugins.nchenouard.tvdenoising.TVDenoising;
import plugins.nchenouard.tvdenoising.TVFISTA;
import plugins.adufour.filtering.Convolution1D;
import plugins.adufour.filtering.ConvolutionException;
import plugins.adufour.filtering.Kernels1D;


/**
 * @author Perrine
 *
 */
public class Preprocess3Dstackto2D extends EzPlug  {
	EzVarSequence source;
	EzVarBoolean Applytoallchanels=new EzVarBoolean("STEP 1: Do you want to apply it to all channels: ",true);
	Sequence tobeprocessed; // added to made it more robust to any crazy click on another image while having selected the Active Sequence Option
	EzVarBoolean denoise=new EzVarBoolean("STEP 2: Do you want to denoise:", false);
	EzVarBoolean flatten=new EzVarBoolean("STEP 3: Do you want to flatten the image:", false);
	EzVarText choicemethodflatten = new EzVarText("Method of flatenning to be used", new String[] {
			"Do a maximum intensity projection",
			"Do a minimum intensity projection",
			"Create an optimized in focus slice (EDF EPFL Plugin)" }, 0, false);
	EzVarText choicemethoddenoising = new EzVarText("Level of denoising to be used", new String[] {
			"small make up",
			"big make-over"
			 }, 0, false);
	EzVarDouble choiceobjectsize = new EzVarDouble("Approximate diameter of object (for bg removal) in pixel", 10.0, 0.0, 500,100);
	EzVarText choicechannel ;
	String[] listofChannelnames;
	@Override
	public void clean() {
		
		
	}

	@Override
	protected void execute() {
		if(source.getValue() == null) {
			MessageDialog.showDialog("Source was closed. Please open one and try again");
			throw new RuntimeException("Source was closed. Please open one and try again");
		}

		tobeprocessed = source.getValue();
		int sizet= tobeprocessed.getSizeT();
		int sizez= tobeprocessed.getSizeZ();
		//Extract Channel if needed
		if (Applytoallchanels.getValue()==false){
			// get Id of of channel name
			int indc=0;
			for (int c=0;c<listofChannelnames.length; c++){
				if (choicechannel.getValue()==listofChannelnames[c]){
					indc=c;
				}
			}
			String extractedchannelname =tobeprocessed.getChannelName(indc);
			tobeprocessed.beginUpdate();
			try{
			Sequence channelextracted=SequenceUtil.extractChannel(tobeprocessed,indc);
			
			tobeprocessed.removeAllImages();
			for (int t=0; t<sizet; t++){
				for (int z=0; z< sizez; z++){
					IcyBufferedImage image= channelextracted.getImage(t, z);
					tobeprocessed.setImage(t, z, image);
					

				}
			}
			
			}
			finally {
				tobeprocessed.endUpdate();
				tobeprocessed.setChannelName(0, extractedchannelname);
				tobeprocessed.setFilename(tobeprocessed.getFilename()+ " ("+extractedchannelname+")");
				tobeprocessed.setName(tobeprocessed.getName()+" ("+extractedchannelname+")");
			}
		}
		// Apply denoising if asked
		if (denoise.getValue()==true){
			int nbchannel=tobeprocessed.getSizeC();
			
			
			double objectdiameter=choiceobjectsize.getValue();
			
			 Sequence duplicate1=SequenceUtil.getCopy(tobeprocessed);
			 
			 duplicate1.beginUpdate();
			Kernels1D gaussianXY = Kernels1D.CUSTOM_GAUSSIAN.createGaussianKernel1D(objectdiameter);
           // Kernels1D gaussianZ = Kernels1D.CUSTOM_GAUSSIAN.createGaussianKernel1D(preFilter * scaleXZ);
            try {
				Convolution1D.convolve(duplicate1, gaussianXY.getData(), gaussianXY.getData(), null);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConvolutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            duplicate1.endUpdate();
            
            tobeprocessed.beginUpdate();
            try{
            	for (int t=0;t<tobeprocessed.getSizeT();t++)
            		for (int z=0;z<tobeprocessed.getSizeZ();z++){
            			IcyBufferedImage image =substractbg(tobeprocessed,duplicate1,t,z);
            			tobeprocessed.setImage(t,z,image);
            		}
            }
            finally{
            	tobeprocessed.endUpdate();
            }
            Sequence duplicate2=SequenceUtil.getCopy(tobeprocessed);
			tobeprocessed.beginUpdate();
			//Sequence denoised=new Sequence();
			try{
			for (int t=0; t<sizet; t++){
				for (int z=0; z< sizez; z++){
					 IcyBufferedImage imagedenoised=new IcyBufferedImage(duplicate2.getWidth(), duplicate2.getHeight(), nbchannel, duplicate2.getDataType_());
			for (int c=0; c<nbchannel;c++){
				

				if (choicemethoddenoising.getValue() == "small make up"){
					
					 
					  IcyBufferedImage imagedenoised2=TVDenoising.regularizeTVImage(duplicate2.getImage(t, z), c, 10, objectdiameter,TVFISTA.RegularizationType.ISOTROPIC );
					  imagedenoised.setDataXY(c,imagedenoised2.getDataXY(0));
					// we keep it as a sequence because Blurred image required the block protocol to be imported as well.
				}
				else if (choicemethoddenoising.getValue() == "big make-over"){
					  IcyBufferedImage imagedenoised2=TVDenoising.regularizeTVImage(duplicate2.getImage(t, z), c, 100, objectdiameter*2,TVFISTA.RegularizationType.ISOTROPIC );
					  imagedenoised.setDataXY(c,imagedenoised2.getDataXY(0));
				}
				//double[] doubleArray = Array1DUtil.arrayToDoubleArray(result.getDataXY(c), result.isSignedDataType());

			
						
						

				
			}
						tobeprocessed.setImage(t, z, imagedenoised);// done twice for nothing 
						
					}

				}
				

			
			
			
			
		}
			finally{
			tobeprocessed.endUpdate();
			tobeprocessed.setFilename(tobeprocessed.getFilename()+ " (denoised)");
			tobeprocessed.setName(tobeprocessed.getName()+ " (denoised)");
			}
		}

			

		
		
		//addSequence(duplicate);
		// apply MAX flatening method if asked 
		if ((choicemethodflatten.getValue() == "Do a maximum intensity projection")
							&& (flatten.getValue() == true))
		{
			tobeprocessed.removeAllROI();
			Sequence duplicate=SequenceUtil.getCopy(tobeprocessed);
			boolean max=true;
			tobeprocessed.removeAllImages();
			tobeprocessed.beginUpdate();
			
			try{
			for (int t=0; t< sizet; t++){
				IcyBufferedImage image2 = getProj(max, duplicate, t);
				tobeprocessed.setImage(t, 0, image2);
				
			}
			
			}
			finally{
			tobeprocessed.endUpdate();
			tobeprocessed.setFilename(tobeprocessed.getFilename()+ " (max projection)");
			tobeprocessed.setName(tobeprocessed.getName()+ " (max projection)");
			}
		}
		
		// apply MIN flatening method if asked 
				if ((choicemethodflatten.getValue() == "Do a minimum intensity projection")
									&& (flatten.getValue() == true))
				{
					tobeprocessed.removeAllROI();
					Sequence duplicate=SequenceUtil.getCopy(tobeprocessed);
					boolean max=false;
					tobeprocessed.removeAllImages();
					tobeprocessed.beginUpdate();
					try{
					
					for (int t=0; t< duplicate.getSizeT(); t++){
						IcyBufferedImage image2 = getProj(max, duplicate, t);
						tobeprocessed.setImage(t, 0, image2);
						
					}
					
					}
					finally{
					tobeprocessed.endUpdate();
					tobeprocessed.setFilename(tobeprocessed.getFilename()+ " (min projection)");
					tobeprocessed.setName(tobeprocessed.getName()+ " (min projection)");
					}
				}
				
				
		// Apply EPFL flatenning method
		if ((choicemethodflatten.getValue() == "Create an optimized in focus slice (EDF EPFL Plugin)")
				&& (flatten.getValue() == true)) {
			// New dialog box before: choose the channel to process, denoise or not and then only call epfl plugin.
			String directory = FileUtil.getApplicationDirectory()+ FileUtil.separator+"ij"+FileUtil.separator+ "plugins" +FileUtil.separator+"EDF";
			if (!(FileUtil.exists(directory))){

			
				new ToolTipFrame(    			
						"<html>"+
								"<br> EPFL ImageJ plugin seems not not be installed"+
								"<br> download it here <a href=\"http://bigwww.epfl.ch/demo/edf/EDF.zip\">http://bigwww.epfl.ch/demo/edf/EDF.zip</a>"+
								"<br> Then unzip it under "+directory+
								"<br> and restart ICY"+
								"</html>"
						);
				return;
			
			}
			ProgressFrame progress = new ProgressFrame("Computing Optimized focus slice...");
			progress.setPosition(0.2);
			tobeprocessed.removeAllROI();
			Sequence[] arrayofimage= new Sequence[tobeprocessed.getSizeC()];
			for (int c=0; c<tobeprocessed.getSizeC(); c++){
			ImagePlus test = ImageJUtil.convertToImageJImage(SequenceUtil.extractChannel(tobeprocessed, c),
					new ProgressListener() {

						@Override
						public boolean notifyProgress(double position,
								double length) {
							// TODO Auto-generated method stub
							return false;
						}
					});
			test.show();
			//TODO check installation and install if needed
			/*File fichierClient = new File("../../ij/pluginsmonFichier.properties");
			//if (!fichierClient.isFile()) {
				try {
					fichierClient.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// IJ.runMacroFile("C:\\Users\\Perrine\\Mes Programmes\\icy_1.3.6.0\\ij\\macros\\MacroEDFEPFL.txt");
			/*InputStream inputStream = Preprocess3Dstackto2D.class.getResourceAsStream("plugins/perrine/easyclemv0/EDF_Easy_.class");
			FileOutputStream fos = null;
			try {
			    fos = new FileOutputStream("somelib.dll");
			    byte[] buf = new byte[2048];
			    int r = inputStream.read(buf);
			    while(r != -1) {
			        fos.write(buf, 0, r);
			        r = inputStream.read(buf);
			    }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			    if(fos != null) {
			        try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			}*/
			
			
			IJ.run("EDF Easy ",
					"quality='4' topology='1' show-topology='off' show-view='off'");// TODO: Mettre un slider sur la qualite en parametres
			
			while (ij.WindowManager.getImageCount() < 2) {
				//System.out.println("running");
			try {
				Thread.sleep( 5 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
			
			
			// show the sequence
			
			ImagePlus test4 = IJ.getImage();
			Sequence test2 = ImageJUtil.convertToIcySequence(test4,
					new ProgressListener() {

						@Override
						public boolean notifyProgress(double position,
								double length) {
							// TODO Auto-generated method stub
							return false;
						}

					});
			test4.close();
			test.close();
			progress.setPosition(c/tobeprocessed.getSizeC());
			IJ.run("Close All");

			// test=IJ.getImage();

			Sequence tmpnew= new Sequence(test2.getImage(0, 0));
			arrayofimage[c]=tmpnew;
			}
			tobeprocessed.beginUpdate();
			tobeprocessed.removeAllImages();
			
			try{
				Sequence tmp= SequenceUtil.concatC(arrayofimage);
				IcyBufferedImage test3= tmp.getImage(0, 0);
					
			tobeprocessed.setImage(0, 0, test3);
			
			}
			finally{
			tobeprocessed.endUpdate();
			tobeprocessed.setFilename(tobeprocessed.getFilename()+ " (Focused)");
			tobeprocessed.setName(tobeprocessed.getName()+ " (Focused)");
			}
			progress.setPosition(1);
			progress.close();

		}
		
	}

	private IcyBufferedImage substractbg(Sequence ori, Sequence bg,int t, int z) {
		
				IcyBufferedImage result= new IcyBufferedImage(ori.getSizeX(), ori.getSizeY(),ori.getSizeC(), ori.getDataType_());
				for (int c=0;c<ori.getSizeC();c++){
					Object dataArrayori = ori.getDataXY(t, z, c);
					Object dataArrayBg = bg.getDataXY(t, z, c);
					double[] imgDoubleArray = Array1DUtil.arrayToDoubleArray(dataArrayori, ori.isSignedDataType());
					double[] imgDoubleArraybg = Array1DUtil.arrayToDoubleArray(dataArrayBg, bg.isSignedDataType());
					double[] dummyzeros=Array1DUtil.arrayToDoubleArray(result.getDataXY(c), result.isSignedDataType());
					ArrayMath.subtract(imgDoubleArray,imgDoubleArraybg,imgDoubleArray);
					ArrayMath.max(imgDoubleArray,dummyzeros,imgDoubleArray);
					Array1DUtil.doubleArrayToArray(imgDoubleArray,result.getDataXY(c));
				}
				result.dataChanged();
			
	
		return result;
	}

	@Override
	protected void initialize() {
		
		EzLabel textinfo=new EzLabel("Please select the image on which you want to preprocess (likely Light microscopy Image).");
		//EzLabel textinfo2=new EzLabel(
		textinfo.setToolTipText("Once the transform will have been computed on this image, \n you can apply it to the full stack afterwards using ApplyTransformation.");
		source=new EzVarSequence("Select Source Image (will be closed and replaced)");
		addEzComponent(textinfo);
		//addEzComponent(textinfo2);
		addEzComponent(source);
		EzLabel textinfo3=new EzLabel("Select the pre-processing to be applied.");
		textinfo3.setToolTipText("Once the transform will have been computed on this image, \n you can apply it to the full stack afterwards using ApplyTransformation.");
		addEzComponent(textinfo3);
		
		listofChannelnames=new String[source.getValue().getSizeC()];
		for (int i=0;i<source.getValue().getSizeC();i++){
			listofChannelnames[i]=source.getValue().getChannelName(i);
		}
		choicechannel= new EzVarText("Work on Channel", listofChannelnames
				 , 0, false);
		
		Applytoallchanels.setToolTipText("if unchecked you will have to select the channel on which it has to be applied \n (i.e the most useful to help the alignment)");
		flatten.setToolTipText("a 3D stack will become an optimized 2D image");
		addEzComponent(Applytoallchanels);
		addEzComponent(choicechannel);
		addEzComponent(denoise);
		addEzComponent(choicemethoddenoising);
		addEzComponent(choiceobjectsize);
		denoise.setToolTipText(" This will launch the Edge preserving smoothing (TV regularization) denoising plugin with some predefined settings");
		
		addEzComponent(flatten);
		addEzComponent(choicemethodflatten);
		
		if (source.getValue().getSizeZ()>1){
			flatten.setVisible(true);
			choicemethodflatten.setVisible(true);
			flatten.addVisibilityTriggerTo(choicemethodflatten, true);
		}
		if (source.getValue().getSizeZ()==1){
			
			flatten.setVisible(false);
			choicemethodflatten.setVisible(false);
			//flatten.addVisibilityTriggerTo(choicemethodflatten, true);
		}
		
		denoise.addVisibilityTriggerTo(choicemethoddenoising, true);
		denoise.addVisibilityTriggerTo(choiceobjectsize,true);
		Applytoallchanels.addVisibilityTriggerTo(choicechannel, false);
		
		 
		EzVarListener<Sequence> seqlistener =new EzVarListener<Sequence>()
				{
			@Override
			public  void variableChanged(EzVar<Sequence> source, Sequence newseq ){
				if (source.getValue()!= null){
				listofChannelnames=new String[source.getValue().getSizeC()];
				for (int i=0;i<source.getValue().getSizeC();i++){
					listofChannelnames[i]=source.getValue().getChannelName(i);
				}
				choicechannel.setDefaultValues(listofChannelnames, 0, false);
				if (source.getValue().getSizeZ()>1){
					flatten.setVisible(true);
					choicemethodflatten.setVisible(true);
					flatten.addVisibilityTriggerTo(choicemethodflatten, true);
				}
				if (source.getValue().getSizeZ()==1){
					
					flatten.setVisible(false);
					choicemethodflatten.setVisible(false);
					//flatten.addVisibilityTriggerTo(choicemethodflatten, true);
				}
			}
			}
		};
		
		source.addVarChangeListener(seqlistener);
		
	}
	
	IcyBufferedImage getProj(boolean max, Sequence sequence,int t){
		IcyBufferedImage result= new IcyBufferedImage(sequence.getSizeX(), sequence.getSizeY(),sequence.getSizeC(), sequence.getDataType_());
		if (!max)
			result.setDataXY(0,sequence.getDataCopyCXY(t,0));// in order to get it different from 0, otherwise min is always 0...
		
			
		for (int c=0;c<sequence.getSizeC();c++){
			double[] doubleArray = Array1DUtil.arrayToDoubleArray(result.getDataXY(c), result.isSignedDataType());
		for (int z = 0; z<sequence.getSizeZ();z++){
			project(max, sequence,t,z,c, doubleArray);
		}
			Array1DUtil.doubleArrayToArray(doubleArray, result.getDataXY(c));
		
		
		}
		result.dataChanged();
		return result;
		
		
	}
	void project(boolean max, Sequence sequence, int t, int z, int c, double[] result){
		Object dataArray = sequence.getDataXY(t, z, c);
		double[] imgDoubleArray = Array1DUtil.arrayToDoubleArray(dataArray, sequence.isSignedDataType());
		if (max)
			ArrayMath.max(result, imgDoubleArray, result);
		else
			ArrayMath.min(result, imgDoubleArray, result);
	}

}
