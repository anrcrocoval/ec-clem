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
package plugins.fr.univ_nantes.ec_clem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import icy.roi.ROI;
import icy.vtk.VtkUtil;
import plugins.adufour.roi.mesh.polygon.ROI3DPolygonalMesh;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceFactory;
import plugins.fr.univ_nantes.ec_clem.sequence_listener.FiducialRoiListener;
import plugins.fr.univ_nantes.ec_clem.sequence_listener.RoiListenerManager;
import plugins.fr.univ_nantes.ec_clem.sequence_listener.SequenceListenerUtil;
import plugins.fr.univ_nantes.ec_clem.transformation.configuration.TransformationConfigurationFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;
import plugins.fr.univ_nantes.ec_clem.ui.GuiCLEMButtons;
import plugins.fr.univ_nantes.ec_clem.ui.GuiCLEMButtons2;
import plugins.fr.univ_nantes.ec_clem.workspace.Workspace;
import icy.gui.viewer.Viewer;
import icy.main.Icy;
import icy.system.thread.ThreadUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.ezplug.EzVarText;
import plugins.adufour.vars.lang.VarSequence;
import plugins.kernel.roi.descriptor.measure.ROIMassCenterDescriptorsPlugin;
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
import plugins.fr.univ_nantes.ec_clem.misc.GuiCLEMButtonApply;
import plugins.fr.univ_nantes.ec_clem.misc.advancedmodules;
import vtk.*;

import javax.inject.Inject;

public class EasyCLEMv0 extends EzPlug implements EzStoppable, Block {

	private MessageOverlay messageSource = new MessageOverlay("Source");
	private MessageOverlay messageTarget = new MessageOverlay("Target");

	private TransformationConfigurationFactory transformationConfigurationFactory;
	private SequenceFactory sequenceFactory;
	private RoiListenerManager roiListenerManager;
	private GuiCLEMButtons guiCLEMButtons;
	private GuiCLEMButtons2 rigidspecificbutton;

	public EasyCLEMv0() {
		DaggerEasyCLEMv0Component.builder().build().inject(this);
	}

	@Inject
	public void setTransformationConfigurationFactory(TransformationConfigurationFactory transformationConfigurationFactory) {
		this.transformationConfigurationFactory = transformationConfigurationFactory;
	}

	@Inject
	public void setSequenceFactory(SequenceFactory sequenceFactory) {
		this.sequenceFactory = sequenceFactory;
	}

	@Inject
	public void setRoiListenerManager(RoiListenerManager roiListenerManager) {
		this.roiListenerManager = roiListenerManager;
	}

	@Inject
	public void setGuiCLEMButtons(GuiCLEMButtons guiCLEMButtons) {
		this.guiCLEMButtons = guiCLEMButtons;
	}

	@Inject
	public void setRigidspecificbutton(GuiCLEMButtons2 rigidspecificbutton) {
		this.rigidspecificbutton = rigidspecificbutton;
	}

	private Workspace workspace;
	private EzVarText choiceinputsection = new EzVarText(
		"I want to compute the transformation in:",
		(String[]) (Arrays.stream(TransformationType.values()).map(variant -> variant.name()).collect(Collectors.toList())).toArray(),
		0, false
	);
	private EzVarText noiseModel = new EzVarText(
		"Noise model:",
		(String[]) (Arrays.stream(NoiseModel.values()).map(variant -> variant.name()).collect(Collectors.toList())).toArray()
		, 0, false
	);
	private EzVarBoolean showgrid = new EzVarBoolean(" Show grid deformation", true);
	private EzVarSequence target = new EzVarSequence("Select image that will not be modified (likely EM)");
	private EzVarSequence source = new EzVarSequence("Select image that will be transformed and resized (likely FM)");
	private EzGroup inputGroup = new EzGroup("Images to process", source, target, choiceinputsection, noiseModel, showgrid);
	private VarSequence tseqsource=new VarSequence("Source transformed on Target sequence", null);
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
		new ToolTipFrame("<html>" + "<br> Press Play when ready. " + "<br> <li> Add point (2D or 3D ROI) on any image and drag it to its correct position in the other image.</li> "
				+ "<br> <li> Use zoom and Lock views to help you!</li> "
				+ "</html>","startmessage");
		addEzComponent(new EzLabel(getVersionString()));
		addComponent(new GuiCLEMButtonApply());
		addComponent(new advancedmodules(this));
		addEzComponent(inputGroup);

		choiceinputsection.setToolTipText("2D transform will be only in the plane XY " + "but can be applied to all dimensions.\n WARNING make sure to have the metadata correctly set in 3D");
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

		if (!this.isHeadLess()) {
			source.setEnabled(false);
			target.setEnabled(false);
			choiceinputsection.setEnabled(false);
			noiseModel.setEnabled(false);
			showgrid.setEnabled(false);
		}
		if (!getchoice().equals(INPUT_SELECTION_RIGID)) {
			rigidspecificbutton.removespecificrigidbutton();
		}
		String name = sourceSequence.getFilename()+ "_to_"+targetSequence.getName()+"_points.xml";
		String nametransfo = sourceSequence.getFilename() +"_to_"+targetSequence.getName()+ "_transfo.xml";
		sourceSequence.setName(sourceSequence.getName() + "_transformed");
		System.out.println("Resulting Transformation will be saved as:");
		System.out.println(nametransfo);
		System.out.println("and points used for the registration as:");
		System.out.println(name);
		if (this.isHeadLess())
			showgrid.setValue(false);
		workspace = new Workspace();
		workspace.setSourceSequence(sourceSequence);
		workspace.setTargetSequence(targetSequence);
		workspace.setSourceBackup(SequenceUtil.getCopy(sourceSequence));
		workspace.setXMLFile(new File(name));
		workspace.setXMLFileTransfo(new File(nametransfo));
		workspace.setTransformationConfiguration(
			transformationConfigurationFactory.getFrom(
				TransformationType.valueOf(choiceinputsection.getValue()),
				NoiseModel.valueOf(noiseModel.getValue()),
				showgrid.getValue()
			)
		);

		guiCLEMButtons.setworkspace(workspace);
		rigidspecificbutton.setWorkspace(workspace);

//		sourceSequence.addListener(sourceSequenceFiducialRoiListener.setSequence(targetSequence).setWorkspaceState(workspace.getWorkspaceState()));
//		targetSequence.addListener(targetSequenceFiducialRoiListener.setSequence(sourceSequence).setWorkspaceState(workspace.getWorkspaceState()));

		sourceSequence.addOverlay(messageSource);
		targetSequence.addOverlay(messageTarget);
		sourceSequence.setFilename(sourceSequence.getName() + ".tif");

		if (!this.isHeadLess()) {
			new AnnounceFrame("Select point on image" + targetSequence.getName() + ", then drag it on source image and RIGHT CLICK", 5);
			guiCLEMButtons.setEnabled(true);
			rigidspecificbutton.enableButtons();
			Icy.getMainInterface().setSelectedTool(ROI3DPointPlugin.class.getName());
		} else {
			 WorkspaceTransformer workspaceTransformer = new WorkspaceTransformer(workspace);
			 workspaceTransformer.run();
			 tseqsource.setValue(workspace.getSourceSequence());
			 return;
		}
		
		synchronized(this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

//		roiListenerManager.clear();
		sourceSequence.removeOverlay(messageSource);
		targetSequence.removeOverlay(messageTarget);
	}
	
	@Override
	public void clean() {}

	@Override
	public void stopExecution() {
		ThreadUtil.invokeLater(() -> new Viewer(
				sequenceFactory.getMergeSequence(workspace.getSourceSequence(), workspace.getTargetSequence())
		));
		guiCLEMButtons.setEnabled(false);
		rigidspecificbutton.disableButtons();
		source.setEnabled(true);
		target.setEnabled(true);
		choiceinputsection.setEnabled(true);
		noiseModel.setEnabled(true);
		showgrid.setEnabled(true);
		synchronized(this) {
			notify();
		}
	}

	@Override
	public void declareInput(VarList inputMap) {
		inputMap.add("Source Image", source.getVariable());
		inputMap.add("Target Image", target.getVariable());
		inputMap.add("Transformation Mode", choiceinputsection.getVariable());
	}

	@Override
	public void declareOutput(VarList outputMap) {
		outputMap.add("Source Transformed on Target", tseqsource );
		
	}
}
