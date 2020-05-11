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
package plugins.fr.univ_nantes.ec_clem.ec_clem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import plugins.adufour.ezplug.*;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.configuration.TransformationConfigurationFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.ui.GuiCLEMButtons;
import plugins.fr.univ_nantes.ec_clem.ec_clem.ui.GuiCLEMButtons2;
import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.Workspace;
import icy.main.Icy;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.frame.progress.ToolTipFrame;
import icy.gui.util.FontUtil;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import plugins.fr.univ_nantes.ec_clem.ec_clem.misc.advancedmodules;
import javax.inject.Inject;

public class EasyCLEMv0 extends EzPlug implements EzStoppable {

	private MessageOverlay messageSource = new MessageOverlay("Source");
	private MessageOverlay messageTarget = new MessageOverlay("Target");

	private TransformationConfigurationFactory transformationConfigurationFactory;
	private GuiCLEMButtons guiCLEMButtons;
	private GuiCLEMButtons2 rigidspecificbutton;

	private Workspace workspace;
	private EzVarText choiceinputsection = new EzVarText(
		"Transformation model:",
		TransformationType.toArray(),
		0, false
	);
	private EzVarText noiseModel = new EzVarText(
		"Noise model:",
		NoiseModel.toArray(),
		0, false
	);
	private EzVarBoolean showgrid = new EzVarBoolean(" Show grid deformation", false);
	private EzVarSequence target = new EzVarSequence("Target sequence");
	private EzVarSequence source = new EzVarSequence("Source sequence");
	private EzGroup inputGroup = new EzGroup("Images to process", source, target, choiceinputsection, noiseModel, showgrid);

	public static Color[] Colortab = new Color[] {
		Color.RED,
		Color.YELLOW,
		Color.PINK,
		Color.GREEN,
		Color.BLUE,
		Color.CYAN,
		Color.LIGHT_GRAY,
		Color.MAGENTA,
		Color.ORANGE
	};

	public EasyCLEMv0() {
		DaggerEasyCLEMv0Component.builder().build().inject(this);
	}

	private class MessageOverlay extends Overlay {
		String message;

		public MessageOverlay(String text) {
			super("Message");
			message = text;
		}

		@Override
		public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
			if ((canvas instanceof IcyCanvas2D) && (g != null)) {
				g.setColor(Color.RED);
				g.setStroke(new BasicStroke(5));
				Font f = g.getFont();
				f = FontUtil.setName(f, "Arial");
				f = FontUtil.setSize(f, (int) canvas.canvasToImageLogDeltaX(20));
				g.setFont(f);
				g.drawString(message, 10, (int) canvas.canvasToImageLogDeltaX(50));
			}
		}
	}

	private String getVersionString() {
		String className = this.getClass().getSimpleName() + ".class";
		String classPath = this.getClass().getResource(className).toString();
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
		Manifest manifest = null;
		try {
			manifest = new Manifest(new URL(manifestPath).openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Attributes attributes = manifest.getMainAttributes();
		return String.format("Version : %s",
			attributes.getValue("Implementation-Version")
		);
	}

	@Override
	protected void initialize() {
		new ToolTipFrame(
	"<html>"
			+ "<br> Press Play when ready. "
			+ "<br> <li> Add point (2D or 3D ROI) on any image and drag it to its correct position in the other image.</li> "
			+ "<br> <li> Use zoom and Lock views to help you!</li> "
			+ "</html>","startmessage"
		);
		addEzComponent(new EzLabel(getVersionString()));
//		addComponent(new GuiCLEMButtonApply());
		addComponent(new advancedmodules(this));
		addEzComponent(inputGroup);

		guiCLEMButtons.setEnabled(false);
		addComponent(guiCLEMButtons);
		rigidspecificbutton.disableButtons();
		addComponent(rigidspecificbutton);
	}

	@Override
	protected void execute() {
		Sequence sourceSequence = source.getValue();
		Sequence targetSequence = target.getValue();
		if (sourceSequence == targetSequence) {
			MessageDialog.showDialog("You have selected the same sequence for target sequence and source sequence. \n Check the IMAGES to PROCESS selection");
			return;
		}
		if (sourceSequence == null) {
			MessageDialog.showDialog("No sequence selected for Source. \n Check the IMAGES to PROCESS selection");
			return;
		}
		if (targetSequence == null) {
			MessageDialog.showDialog("No sequence selected for Target. \n Check the IMAGES to PROCESS selection");
			return;
		}

		source.setEnabled(false);
		target.setEnabled(false);
		choiceinputsection.setEnabled(false);
		noiseModel.setEnabled(false);
		showgrid.setEnabled(false);

		workspace = new Workspace();
		workspace.setSourceSequence(sourceSequence);
		workspace.setTargetSequence(targetSequence);
		workspace.setSourceBackup(SequenceUtil.getCopy(sourceSequence));
		Path parent = Paths.get(sourceSequence.getFilename()).getParent();
		LocalDateTime date = LocalDateTime.now();

		File transformationSchemaOutputFile = new File(String.format("%s/%s_to_%s_%s.transformation_schema.xml", parent.toString(), sourceSequence.getName(), targetSequence.getName(), date.toString()));
		System.out.println(String.format("Transformation schema saved at : %s", transformationSchemaOutputFile.toString()));
		workspace.setTransformationSchemaOutputFile(transformationSchemaOutputFile);

		File csvTransformationFile = new File(String.format("%s/%s_to_%s_%s.transformation.csv", parent.toString(), sourceSequence.getName(), targetSequence.getName(), date.toString()));
		System.out.println(String.format("CSV format transformation saved at : %s", csvTransformationFile.toString()));
		workspace.setCsvTransformationOutputFile(csvTransformationFile);
		
		File XmlTransformationFile = new File(String.format("%s/%s_to_%s_%s.transformation.xml", parent.toString(), sourceSequence.getName(), targetSequence.getName(), date.toString()));
		System.out.println(String.format("XML format transformation saved at : %s", XmlTransformationFile.toString()));
		workspace.setXmlTransformationOutputFile(XmlTransformationFile);

		workspace.setTransformationConfiguration(
			transformationConfigurationFactory.getFrom(
				TransformationType.valueOf(choiceinputsection.getValue()),
				NoiseModel.valueOf(noiseModel.getValue()),
				showgrid.getValue()
			)
		);

		guiCLEMButtons.setworkspace(workspace);
		rigidspecificbutton.setWorkspace(workspace);

		sourceSequence.addOverlay(messageSource);
		targetSequence.addOverlay(messageTarget);
		sourceSequence.setFilename(sourceSequence.getName() + ".tif");

		new ToolTipFrame(
	"<html>"
			+ "<br> Set at least 3 fiducial points and click on <b>Update transformation</b> button. "
			+ "<br> Set at least 7 fiducial points to unlock error estimation capabilities. "
			+ "</html>","runmessage"
		);

		guiCLEMButtons.setEnabled(true);
		rigidspecificbutton.enableButtons();
		Icy.getMainInterface().setSelectedTool(ROI3DPointPlugin.class.getName());

		synchronized(this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		sourceSequence.removeOverlay(messageSource);
		targetSequence.removeOverlay(messageTarget);
	}
	
	@Override
	public void clean() {
		clearWorkspace();
	}

	@Override
	public void stopExecution() {
		guiCLEMButtons.setEnabled(false);
		rigidspecificbutton.disableButtons();
		source.setEnabled(true);
		target.setEnabled(true);
		choiceinputsection.setEnabled(true);
		noiseModel.setEnabled(true);
		showgrid.setEnabled(true);
		clearWorkspace();
		synchronized(this) {
			notify();
		}
	}

	private void clearWorkspace() {
		if(workspace != null) {
			workspace.setSourceSequence(null);
			workspace.setTargetSequence(null);
			workspace.setSourceBackup(null);
			workspace.setTransformationConfiguration(null);
			workspace.setTransformationSchema(null);
			workspace.setCsvTransformationOutputFile(null);
			workspace.setXmlTransformationOutputFile(null);
			workspace.setTransformationSchemaOutputFile(null);
			workspace = null;
		}
	}

	@Inject
	public void setTransformationConfigurationFactory(TransformationConfigurationFactory transformationConfigurationFactory) {
		this.transformationConfigurationFactory = transformationConfigurationFactory;
	}

	@Inject
	public void setGuiCLEMButtons(GuiCLEMButtons guiCLEMButtons) {
		this.guiCLEMButtons = guiCLEMButtons;
	}

	@Inject
	public void setRigidspecificbutton(GuiCLEMButtons2 rigidspecificbutton) {
		this.rigidspecificbutton = rigidspecificbutton;
	}
}
