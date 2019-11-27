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
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import plugins.fr.univ_nantes.ec_clem.sequence.SequenceFactory;
import plugins.fr.univ_nantes.ec_clem.sequence_listener.RoiDuplicator;
import plugins.fr.univ_nantes.ec_clem.sequence_listener.SequenceListenerUtil;
import plugins.fr.univ_nantes.ec_clem.transformation.configuration.TransformationConfigurationFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;
import plugins.fr.univ_nantes.ec_clem.ui.GuiCLEMButtons;
import plugins.fr.univ_nantes.ec_clem.ui.GuiCLEMButtons2;
import plugins.fr.univ_nantes.ec_clem.workspace.Workspace;
import icy.gui.viewer.Viewer;
import icy.main.Icy;
import icy.system.thread.ThreadUtil;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.ezplug.EzVarText;
import plugins.kernel.roi.descriptor.measure.ROIMassCenterDescriptorsPlugin;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.frame.progress.ToolTipFrame;
import icy.gui.util.FontUtil;
import icy.painter.Overlay;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.type.point.Point5D;
import plugins.kernel.roi.roi3d.plugin.ROI3DPointPlugin;
import plugins.fr.univ_nantes.ec_clem.misc.GuiCLEMButtonApply;
import plugins.fr.univ_nantes.ec_clem.misc.advancedmodules;

import javax.inject.Inject;

public class EasyCLEMv0 extends EzPlug implements EzStoppable {

	private MessageOverlay messageSource = new MessageOverlay("Source");
	private MessageOverlay messageTarget = new MessageOverlay("Target");

	private TransformationConfigurationFactory transformationConfigurationFactory;
	private SequenceFactory sequenceFactory;
	private SequenceListenerUtil sequenceListenerUtil;
	private GuiCLEMButtons guiCLEMButtons;
	private GuiCLEMButtons2 rigidspecificbutton;
	private RoiDuplicator sourceSequenceRoiDuplicator;
	private RoiDuplicator targetSequenceRoiDuplicator;

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
	public void setSequenceListenerUtil(SequenceListenerUtil sequenceListenerUtil) {
		this.sequenceListenerUtil = sequenceListenerUtil;
	}

	@Inject
	public void setGuiCLEMButtons(GuiCLEMButtons guiCLEMButtons) {
		this.guiCLEMButtons = guiCLEMButtons;
	}

	@Inject
	public void setRigidspecificbutton(GuiCLEMButtons2 rigidspecificbutton) {
		this.rigidspecificbutton = rigidspecificbutton;
	}

	@Inject
	public void setSourceSequenceRoiDuplicator(RoiDuplicator sourceSequenceRoiDuplicator) {
		this.sourceSequenceRoiDuplicator = sourceSequenceRoiDuplicator;
	}

	@Inject
	public void setTargetSequenceRoiDuplicator(RoiDuplicator targetSequenceRoiDuplicator) {
		this.targetSequenceRoiDuplicator = targetSequenceRoiDuplicator;
	}

	private Workspace workspace;
	private Overlay myoverlaysource;
	private Overlay myoverlaytarget;

	static String[] listofRegistrationchoice = new String[] { "From Live to EM", "From Section to EM", "From Live to Section" };

	private static String INPUT_SELECTION_RIGID = TransformationType.RIGID.name();
	private static String INPUT_SELECTION_SIMILARITY = TransformationType.SIMILARITY.name();
	private static String INPUT_SELECTION_AFFINE = TransformationType.AFFINE.name();
	private static String INPUT_SELECTION_SPLINE = TransformationType.SPLINE.name();
	private static String MESSAGE_SELECTION_RIGID = TransformationType.RIGID.name();
	private static String MESSAGE_SELECTION_SIMILARITY = TransformationType.SIMILARITY.name();
	private static String MESSAGE_SELECTION_AFFINE = TransformationType.AFFINE.name();
	private static String MESSAGE_SELECTION_SPLINE = TransformationType.SPLINE.name();
	private EzVarText choiceinputsection = new EzVarText(
		"I want to compute the transformation in:",
			new String[] {
				MESSAGE_SELECTION_RIGID,
				MESSAGE_SELECTION_SIMILARITY,
				MESSAGE_SELECTION_AFFINE,
				MESSAGE_SELECTION_SPLINE
			}, 0, false
	);
	private EzVarBoolean showgrid = new EzVarBoolean(" Show grid deformation", true);
	private EzVarSequence target = new EzVarSequence("Select image that will not be modified (likely EM)");
	private EzVarSequence source = new EzVarSequence("Select image that will be transformed and resized (likely FM)");
	private EzGroup inputGroup = new EzGroup("Images to process", source, target, choiceinputsection, showgrid);

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

	private class VisiblepointsOverlay extends Overlay {
		public VisiblepointsOverlay() {
			super("Visible points");
		}

		@Override
		public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
			if ((canvas instanceof IcyCanvas2D) && (g != null)) {
				ArrayList<ROI> listfiducials = sequence.getROIs();
				for (ROI roi : listfiducials) {
					Point5D p3D = ROIMassCenterDescriptorsPlugin.computeMassCenter(roi);
					if (Double.isNaN(p3D.getX())) {
						p3D = roi.getPosition5D();
					}

					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(5));
					Font f = g.getFont();
					f = FontUtil.setName(f, "Arial");
					f = FontUtil.setSize(f, (int) canvas.canvasToImageLogDeltaX(20));
					g.setFont(f);
					g.drawString(roi.getName(), (float) p3D.getX(), (float) p3D.getY());
					g.setColor(Color.YELLOW);
					g.drawString(roi.getName(), (float) p3D.getX() + 1, (float) p3D.getY() + 1);
				}
			}
		}
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
		new ToolTipFrame("<html>" + "<br> Press Play when ready. " + "<br> <li> Add point (2D or 3D ROI) on any image and drag it to its correct position in the other image.</li> "
				+ "<br> <li> Use zoom and Lock views to help you!</li> "
				+ "</html>","startmessage");
		addEzComponent(new EzLabel(getVersionString()));
		addComponent(new GuiCLEMButtonApply());
		addComponent(new advancedmodules(this));
		addEzComponent(inputGroup);

		choiceinputsection.setToolTipText("2D transform will be only in the plane XY " + "but can be applied to all dimensions.\n WARNING make sure to have the metadata correctly set in 3D");
		guiCLEMButtons.disableButtons();
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
		showgrid.setEnabled(false);

//		if (!getchoice().equals(INPUT_SELECTION_RIGID)) {
//			rigidspecificbutton.removespecificrigidbutton();
//		}

		sourceSequence.setName(sourceSequence.getName() + "_transformed");
		String name = sourceSequence.getName() + "_transfo.xml";

		workspace = new Workspace();
		workspace.setSourceSequence(sourceSequence);
		workspace.setTargetSequence(targetSequence);
		workspace.setSourceBackup(SequenceUtil.getCopy(sourceSequence));
		workspace.setXMLFile(new File(name));
		workspace.setTransformationConfiguration(transformationConfigurationFactory.getFrom(TransformationType.valueOf(getchoice()), showgrid.getValue()));

		guiCLEMButtons.setworkspace(workspace);
		rigidspecificbutton.setWorkspace(workspace);

		sourceSequence.addListener(sourceSequenceRoiDuplicator.setSequence(targetSequence).setWorkspaceState(workspace.getWorkspaceState()));
		targetSequence.addListener(targetSequenceRoiDuplicator.setSequence(sourceSequence).setWorkspaceState(workspace.getWorkspaceState()));

		myoverlaysource = new VisiblepointsOverlay();
		myoverlaytarget = new VisiblepointsOverlay();
		sourceSequence.addOverlay(myoverlaysource);
		targetSequence.addOverlay(myoverlaytarget);
		sourceSequence.addOverlay(messageSource);
		targetSequence.addOverlay(messageTarget);
		sourceSequence.setFilename(sourceSequence.getName() + ".tif");
		new AnnounceFrame("Select point on image" + targetSequence.getName() + ", then drag it on source image and RIGHT CLICK", 5);

		guiCLEMButtons.enableButtons();
		rigidspecificbutton.enableButtons();
		Icy.getMainInterface().setSelectedTool(ROI3DPointPlugin.class.getName());

		synchronized(this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

//		sourceSequence.removeListener(roiChangedListener);
		sequenceListenerUtil.removeListeners(sourceSequence, RoiDuplicator.class);
		sequenceListenerUtil.removeListeners(targetSequence, RoiDuplicator.class);
		sourceSequence.removeOverlay(myoverlaysource);
		targetSequence.removeOverlay(myoverlaytarget);
		sourceSequence.removeOverlay(messageSource);
		targetSequence.removeOverlay(messageTarget);
	}

	private String getchoice() {
		if (choiceinputsection.getValue().equals(MESSAGE_SELECTION_RIGID))
			return INPUT_SELECTION_RIGID;
		if (choiceinputsection.getValue().equals(MESSAGE_SELECTION_SIMILARITY))
			return INPUT_SELECTION_SIMILARITY;
		if (choiceinputsection.getValue().equals(MESSAGE_SELECTION_AFFINE))
			return INPUT_SELECTION_AFFINE;
		return INPUT_SELECTION_SPLINE;
	}
	
	@Override
	public void clean() {
	}

	@Override
	public void stopExecution() {
		ThreadUtil.invokeLater(() -> new Viewer(
				sequenceFactory.getMergeSequence(workspace.getSourceSequence(), workspace.getTargetSequence())
		));
		guiCLEMButtons.disableButtons();
		rigidspecificbutton.disableButtons();
		source.setEnabled(true);
		target.setEnabled(true);
		choiceinputsection.setEnabled(true);
		showgrid.setEnabled(true);
//		rigidspecificbutton.reshowspecificrigidbutton();
		synchronized(this) {
			notify();
		}
	}
}
