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
package plugins.perrine.easyclemv0.misc;

import java.io.BufferedReader;

import java.io.FileNotFoundException;



import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.ToolTipFrame;
import icy.preferences.ApplicationPreferences;

import plugins.kernel.roi.roi3d.ROI3DPoint;
import icy.sequence.Sequence;
import icy.type.point.Point5D;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarFile;

import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.ezplug.EzVarText;

import java.io.FileReader;
import java.io.IOException;
/**
 * ec-clem utility: read a csv file and import ROI to the selected sequence as roi point, in the same order
 * 
 * @author paul-gilloteaux-p
 *
 */
public class ImportRoiPointsFromFile extends EzPlug implements Block{

	private EzVarSequence source=new EzVarSequence("sequence");

	private EzVarFile csvfile=new EzVarFile(" csv file)", ApplicationPreferences.getPreferences().node("frame/imageLoader").get("path", "."));;
	
	private double converttopixelXY;
	private double converttopixelZ;
	private EzVarText choiceinputsection= new EzVarText("Unit of the points in csv file",
			new String[] { "millimeters", "micrometers","nanometers" ,"pixels" }, 2, false);
	private EzVarText choicefileformat= new EzVarText("csv file content looks like:",
			new String[] { "x;y;z", "x,y,z" }, 1, false);
	private EzVarBoolean choicez= new EzVarBoolean("Z in Slice # (as from IJ/Fiji, starts at 1)",
			false);
	;

	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void execute() {
		Sequence sourceseq=source.getValue();
		String unit=choiceinputsection.getValue();
		if (unit=="millimeters"){
			converttopixelXY=(sourceseq.getPixelSizeX()/1000);
			converttopixelZ=(sourceseq.getPixelSizeZ()/1000);
		}
		if (unit=="nanometers"){
			converttopixelXY=sourceseq.getPixelSizeX()*1000;
			converttopixelZ=sourceseq.getPixelSizeZ()*1000;
		}
		if (unit=="micrometers"){
			converttopixelXY=sourceseq.getPixelSizeX();
			converttopixelZ=sourceseq.getPixelSizeZ();
		}
		if (unit=="pixels"){
			converttopixelXY=1;
			converttopixelZ=1;
		}
		if (sourceseq==null){
			MessageDialog.showDialog("Please make sure that your image is opened");
			return;
		}
		BufferedReader br=null;
		try {

			 br = new BufferedReader(new FileReader(csvfile.getValue()));
			String line;
			String cvsSplitBy = ";";
			if (choicefileformat.getValue()=="x,y,z")
				cvsSplitBy = ",";
			int index=1;
			//converttopixelZ=0;
			try{
			while ((line = br.readLine()) != null) {

			        // use comma as separator
				String[] coordinates = line.split(cvsSplitBy);

				System.out.println("x= " + coordinates[0] 
	                                 + "  y=" + coordinates[1]  + " z="+coordinates[2] );
				
				double x=Double.parseDouble(coordinates[0])/converttopixelXY;
				double y=Double.parseDouble(coordinates[1])/converttopixelXY;
				double z=Double.parseDouble(coordinates[2])/converttopixelZ;
				if (choicez.getValue()==true) // z is is in slice
				{
					System.out.println("z is assumed to be in slice number, will be converted to z-1 " );
					System.out.println(" Example (IJ starting slice numering at 1, starting at 0 in Icy)");
					int offset=1;
					z=Double.parseDouble(coordinates[2])-offset;
				}
				
				ROI3DPoint roi =new ROI3DPoint();
					
					Point5D position = roi.getPosition5D();
					position.setX(x);
					position.setY(y);
					position.setZ(z);
					roi.setPosition5D(position);
					roi.setName("Point "+ index);
					roi.setShowName(true);
					source.getValue().addROI(roi);
				index=index+1;

			}
			} catch (ArrayIndexOutOfBoundsException e) {
				MessageDialog.showDialog("check the format of your file \n (open it in text editor):\n It should be x;y;z or x,y,z \n and that you have selected the right format");
				e.printStackTrace();
			} 
			index=index-1;
			MessageDialog.showDialog("Number of Roi added: "+index );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	protected void initialize() {
		
		new ToolTipFrame(    			
    			"<html>"+
    			"<br>This will load Rois to the image and numbered them in line order. "+
    			"<br>File should be in ascii format (txt or csv for example), with one point per line and "+
    			"<br>a semi column separator. No header."+
    			"<br> x1;y1;z1"+
    			"<br> x2;y2;z2"+
    			" <br> Use Excel for example to edit your file and save your excel file as .csv semi colum"+
    			" <br>Do the same with source and target image and you are ready to apply the transform of your choice"+
				"<br> If \"z in slice number\" is checked, z is assumed to be in slice number, and will be converted to z-1 "+
		         " <br> assuming a starting slice numbered at 1 in IJ, starting at 0 in Icy)"+
    			"</html>","importmessage"
    			);
		

		String varName ="Please select the ROI file (csv format)";
		if (source.getValue()!=null)
			csvfile=new EzVarFile(varName, source.getValue().getFilename());
		else
			csvfile=new EzVarFile(varName, ApplicationPreferences.getPreferences().node("frame/imageLoader").get("path", "."));
		
		addEzComponent(csvfile);
		addEzComponent(choiceinputsection);
		addEzComponent(choicefileformat);
		addEzComponent(choicez);
		addEzComponent(source);
		// we will express everything in pixel, by using a converttopixel factor
		
		
		
		

		
	}

	@Override
	public void declareInput(VarList inputMap) {
		// TODO Auto-generated method stub
		
		inputMap.add("Sequence to process",source.getVariable());
		
		
		inputMap.add("CSV File to import",csvfile.getVariable());
		
		inputMap.add("unit",choiceinputsection.getVariable());
		inputMap.add("format",choicefileformat.getVariable());
		inputMap.add("z fiji",choicez.getVariable());
	}

	@Override
	public void declareOutput(VarList outputMap) {
		// TODO Auto-generated method stub
		outputMap.add("sequence with Rois", source.getVariable());
	}
}
