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
package plugins.perrine.ec_clem.ec_clem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import plugins.adufour.ezplug.*;
import plugins.perrine.ec_clem.ec_clem.transformation.configuration.TransformationConfigurationFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.NoiseModel;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationType;
import plugins.perrine.ec_clem.ec_clem.ui.GuiCLEMButtons;
import plugins.perrine.ec_clem.ec_clem.ui.GuiCLEMButtons2;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;

import icy.main.Icy;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.common.Version;
import icy.file.FileUtil;
import icy.file.Loader;
import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.ToolTipFrame;
import icy.gui.util.FontUtil;
import icy.gui.viewer.Viewer;
import icy.painter.Overlay;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import plugins.perrine.ec_clem.ec_clem.misc.AdvancedmodulesButton;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

public class Ec_clem extends EzPlug implements EzStoppable {

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

	public Ec_clem() {
		DaggerEc_clemComponent.builder().build().inject(this);
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
		Version version= PluginLoader.getPlugin( Ec_clem.class.getName()).getVersion();
		return "version "+ version.toString();
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
		addComponent(new AdvancedmodulesButton());
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
		//TODO add a loop on existing ROI, testing if they have a property set otherwise set one roi.setProperty(POINT_TYPE_PROPERTY, "FIDUCIAL");
		workspace = new Workspace();
		workspace.setSourceSequence(sourceSequence);
		workspace.setTargetSequence(targetSequence);
		Sequence copy = null;
				
		try {
			copy=SequenceUtil.getCopy(sourceSequence);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		workspace.setSourceBackup(copy);
		Path parent= Paths.get(FileUtil.getApplicationDirectory());
		String warningmessagestorage="All saved files will be under "+FileUtil.getApplicationDirectory();
		if (sourceSequence.getFilename()==null ){
			if (targetSequence.getFilename()==null) {
				MessageDialog.showDialog("Note that Source and Target are not saved on disk, \n  "+warningmessagestorage, MessageDialog.WARNING_MESSAGE);
			}
			else {
				parent = Paths.get(targetSequence.getFilename()).getParent();
				MessageDialog.showDialog("Note that Source is not saved on disk, \n  "+warningmessagestorage, MessageDialog.WARNING_MESSAGE);
			}
		}
			
		else {
			parent = Paths.get(sourceSequence.getFilename()).getParent();	
			}
		
		if (parent==null) {
			parent=Paths.get(FileUtil.getApplicationDirectory());
		}
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));

		File transformationSchemaOutputFile = new File(String.format("%s/%s_to_%s_%s.transformation_schema.xml", parent.toString(), sourceSequence.getName(), targetSequence.getName(), date));
		System.out.println(String.format("Transformation schema saved at : %s", transformationSchemaOutputFile.toString()));
		workspace.setTransformationSchemaOutputFile(transformationSchemaOutputFile);

		File csvTransformationFile = new File(String.format("%s/%s_to_%s_%s.transformation.csv", parent.toString(), sourceSequence.getName(), targetSequence.getName(), date));
		System.out.println(String.format("CSV format transformation saved at : %s", csvTransformationFile.toString()));
		workspace.setCsvTransformationOutputFile(csvTransformationFile);
		
		File XmlTransformationFile = new File(String.format("%s/%s_to_%s_%s.transformation.xml", parent.toString(), sourceSequence.getName(), targetSequence.getName(), date));
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
		//why would you do that? sourceSequence.setFilename(sourceSequence.getName() + ".tif");

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
		source.getValue().setFilename(source.getValue().getFilename()+"_transformed.tif");
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
	
	public static void main( final String[] args )
	{
		// Launch the application.
		Icy.main( args );

		/*
		 * // Load an image.
			final String imagePath = "C:\\Users\\perri\\Nextcloud\\Mes documents\\Papierssoumisouencours\\ENCOURS_ori\\AutomaticRegistration43points\\200824\\20200824_DFT_10_10_Aligned_binned_4x4x2.tif";
			final Sequence sequence = Loader.loadSequence( imagePath, 0, true );
			final String imagePath2 = "C:\\Users\\perri\\Nextcloud\\Mes documents\\Papierssoumisouencours\\ENCOURS_ori\\AutomaticRegistration43points\\200824\\C2-roi8.tif";
			final Sequence sequence2 = Loader.loadSequence( imagePath2, 0, true );

			

			// Display the images.
			SwingUtilities.invokeAndWait( () -> {
				new Viewer( sequence );
				new Viewer( sequence2 );
			} );
		 */
		PluginLauncher.start( PluginLoader.getPlugin( Ec_clem.class.getName() ) );
	}
}
