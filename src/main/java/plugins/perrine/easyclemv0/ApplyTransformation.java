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
 * AUthor: Perrine.Paul-Gilloteaux@curie.fr
 * Main Class can be used alone or call from another plugin:
 * will apply the transform content in an xml file as in easyclem
 */

package plugins.perrine.easyclemv0;

import java.io.File;
import java.util.ArrayList;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import Jama.Matrix;
import org.w3c.dom.Element;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.vars.lang.VarSequence;
import plugins.perrine.easyclemv0.image_transformer.ImageTransformer;
import plugins.perrine.easyclemv0.image_transformer.NonRigidTranformationVTK;
import plugins.perrine.easyclemv0.image_transformer.Stack3DVTKTransformer;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.model.Transformation;
import plugins.perrine.easyclemv0.model.WorkspaceTransformer;
import plugins.perrine.easyclemv0.storage.xml.*;
import vtk.vtkDataSet;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.file.Saver;
import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.ProgressFrame;
import icy.preferences.ApplicationPreferences;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzLabel;

import static plugins.perrine.easyclemv0.storage.xml.XmlTransformation.transformationElementName;

public class ApplyTransformation extends EzPlug implements Block {

	private EzVarSequence source = new EzVarSequence("Select Source Image (will be transformed from xml file)");
	private EzVarFile xmlFile=new EzVarFile("Xml file containing list of transformation", ApplicationPreferences.getPreferences().node("frame/imageLoader").get("path", "."));;
	private int extentx;
	private int extenty;
	private int extentz;
	private double spacingx;
	private double spacingy;
	private double spacingz;
	private vtkDataSet[] imageData;
	private double Inputspacingx;
	private double Inputspacingy;
	private double Inputspacingz;
	private Runnable transformer;
	private VarSequence out = new VarSequence("output sequence", null);
	private int auto;

	private XmlFileReader xmlFileReader = new XmlFileReader();
	private XmlTransformationReader xmlTransformationReader = new XmlTransformationReader();
	private WorkspaceTransformer workspaceTransformer = new WorkspaceTransformer();


	@Override
	protected void initialize() {
		EzLabel textinfo = new EzLabel("Please select the image on which you want to apply a transformation, and the xml file containing the transformations (likely your file name _transfo.xml)");
		String varName ="Xml file containing list of transformation";
		if (source.getValue()!=null)
			xmlFile=new EzVarFile(varName, source.getValue().getFilename());
		else
			xmlFile=new EzVarFile(varName, ApplicationPreferences.getPreferences().node("frame/imageLoader").get("path", "."));

		addEzComponent(textinfo);
		addEzComponent(source);
		addEzComponent(xmlFile);
	}

	@Override
	protected void execute() {
		final Sequence sourceseq = source.getValue();
		if (sourceseq == null) {
			MessageDialog.showDialog("Please make sure that your image is opened");
			return;
		}

		transformer = new Runnable() {

	        @Override
	        public void run() {

				Document document = xmlFileReader.loadFile(xmlFile.getValue());
				ArrayList<Element> transformationElements = XMLUtil.getElements(document.getDocumentElement(), transformationElementName);
				for(Element element : transformationElements) {
					Transformation transformation = xmlTransformationReader.read(element);
					//workspaceTransformer.apply(sourceseq, transformation);
				}

				if (!isHeadLess()) {
					IcyCanvas sourcecanvas = source.getValue().getFirstViewer().getCanvas();
					if (sourcecanvas instanceof IcyCanvas2D) {
						((IcyCanvas2D) sourcecanvas).fitCanvasToImage();
					}
				}
				sourceseq.setFilename(sourceseq.getFilename() + " (transformed)");
				sourceseq.setName(sourceseq.getName() + " (transformed)");
				File file = new File(sourceseq.getFilename());
				boolean multipleFiles = false;
				boolean showProgress = true;
				System.out.println("Transformed Image will be saved as " +sourceseq.getFilename());
				Saver.save(sourceseq, file, multipleFiles, showProgress);

				if (!ApplyTransformation.this.isHeadLess()) {
					MessageDialog.showDialog("Transformation have been applied. Image has been renamed and saved, use this one for going on with your alignments");
				}
			}
		};

		if (!this.isHeadLess()){
			ThreadUtil.bgRun(transformer);
		} else {
			ThreadUtil.invokeNow(transformer);
			if (auto!=1) {
				out.setValue(sourceseq);
			}
		}
	}

	@Override
	public void clean() {
	}

	@Override
	public void declareInput(VarList inputMap) {
		inputMap.add("Input Image",source.getVariable());
		inputMap.add("Imput XML File",xmlFile.getVariable());
	}

	@Override
	public void declareOutput(VarList outputMap) {
		outputMap.add("Transformed Sequence", out);
	}
}
