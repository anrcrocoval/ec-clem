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
package plugins.perrine.easyclemv0.ec_clem.misc;

import java.io.File;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.vars.lang.VarSequence;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.file.Saver;
import icy.gui.dialog.MessageDialog;
import icy.preferences.ApplicationPreferences;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzLabel;
import plugins.perrine.easyclemv0.ec_clem.sequence.SequenceUpdater;
import plugins.perrine.easyclemv0.ec_clem.storage.transformation_schema.XmlTransformation;
import plugins.perrine.easyclemv0.ec_clem.storage.transformation_schema.reader.XmlFileReader;
import plugins.perrine.easyclemv0.ec_clem.storage.transformation_schema.reader.XmlTransformationReader;
import plugins.perrine.easyclemv0.ec_clem.transformation.schema.TransformationSchema;

import javax.inject.Inject;

public class ApplyTransformation extends EzPlug implements Block {

	private EzVarSequence source = new EzVarSequence("Select Source Image (will be transformed from storage file)");
	private EzVarFile xmlFile=new EzVarFile("Xml file containing list of transformation", ApplicationPreferences.getPreferences().node("frame/imageLoader").get("path", "."));;
	private VarSequence out = new VarSequence("output sequence", null);
	private SequenceUpdater sequenceUpdater;

	private XmlFileReader xmlFileReader = new XmlFileReader();
	private XmlTransformationReader xmlTransformationReader = new XmlTransformationReader();

	@Inject
	public ApplyTransformation() {}

	@Override
	protected void initialize() {
		EzLabel textinfo = new EzLabel("Please select the image on which you want to apply a transformation, and the storage file containing the transformations (likely your file name _transfo.storage)");
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

		Runnable transformer = () -> {

			Document document = xmlFileReader.loadFile(xmlFile.getValue());
			Element transformationElement = XMLUtil.getElement(document.getDocumentElement(), XmlTransformation.transformationElementName);
			TransformationSchema transformationSchema = xmlTransformationReader.read(transformationElement);
			sequenceUpdater = new SequenceUpdater(sourceseq, transformationSchema);
			sequenceUpdater.run();
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
			System.out.println("Transformed Image will be saved as " + sourceseq.getFilename());
			Saver.save(sourceseq, file, multipleFiles, showProgress);

			if (!ApplyTransformation.this.isHeadLess()) {
				MessageDialog.showDialog("TransformationSchema have been applied. Image has been renamed and saved, use this one for going on with your alignments");
			}
		};

		if (!this.isHeadLess()){
			ThreadUtil.bgRun(transformer);
		} else {
			ThreadUtil.invokeNow(transformer);
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
