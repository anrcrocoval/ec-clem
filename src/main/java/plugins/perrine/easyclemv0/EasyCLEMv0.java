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
 * 
 * Main Class
 **/
package plugins.perrine.easyclemv0;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import icy.gui.viewer.Viewer;
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
import icy.image.IcyBufferedImage;
import icy.painter.Overlay;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.type.DataType;
import icy.type.point.Point5D;
import plugins.perrine.easyclemv0.factory.SequenceFactory;
import plugins.perrine.easyclemv0.factory.TransformationConfigurationFactory;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.sequence_listener.RoiDuplicator;
import plugins.perrine.easyclemv0.ui.GuiCLEMButtons;
import plugins.perrine.easyclemv0.ui.GuiCLEMButtons2;
import plugins.perrine.easyclemv0.util.SequenceListenerUtil;

public class EasyCLEMv0 extends EzPlug implements EzStoppable {

	private Thread currentThread;
	private TransformationConfigurationFactory transformationConfigurationFactory = new TransformationConfigurationFactory();
	private SequenceFactory sequenceFactory = new SequenceFactory();
	private SequenceListenerUtil sequenceListenerUtil = new SequenceListenerUtil();

	private Workspace workspace;

	private Overlay myoverlaysource;
	private Overlay myoverlaytarget;
	private Overlay messageSource;
	private Overlay messageTarget;

	static String[] listofRegistrationchoice = new String[] { "From Live to EM", "From Section to EM", "From Live to Section" };
	private EzLabel versioninfo = new EzLabel("Version " + getDescriptor().getVersion());

	private static String INPUT_SELECTION_2D = "2D (X,Y,[T])";
	private static String INPUT_SELECTION_2D_UPDATE = "2D but let me update myself";
	private static String INPUT_SELECTION_3D = "3D (X,Y,Z,[T])";
	private static String INPUT_SELECTION_3D_UPDATE = "3D but let me update myself";
	private static String INPUT_SELECTION_NON_RIGID = "non rigid (2D or 3D)";
	private static String INPUT_SELECTION_AFFINE = "Affine";
	private EzVarText choiceinputsection = new EzVarText(
		"I want to compute the transformation in:",
			new String[] {
				INPUT_SELECTION_2D,
				INPUT_SELECTION_2D_UPDATE,
				INPUT_SELECTION_3D,
				INPUT_SELECTION_3D_UPDATE,
				INPUT_SELECTION_NON_RIGID,
				INPUT_SELECTION_AFFINE
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

	private GuiCLEMButtons guiCLEMButtons;
	private GuiCLEMButtons2 rigidspecificbutton;

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
		String mytext;

		public MessageOverlay(String text) {
			super("Message");
			mytext = text;
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
				g.drawString(mytext, 10, (int) canvas.canvasToImageLogDeltaX(50));
			}
		}
	}

	@Override
	protected void initialize() {
		new ToolTipFrame("<html>" + "<br> Press Play when ready. " + "<br> <li> Add point (2D or 3D ROI) on target image only.</li> "
				+ "<br> <li> Drag the point in Source, and RIGHT CLICK. Then add point again on target. "
				+ "<br> <li> If you add a point on source image instead (called point2D), delete it, "
				+ "<br> and select the ROI Point to add a point from Target</li> "
				+ "<br> <li> You can also prepare pair of points before , "
				+ "<br> by making sure they will have the same name in both images.</li>"
				+ "<br> <li> Do not forget that the transformation will be automatically saved "
				+ "<br> and that you can apply to any image with the same original or a rescaled dimension.</li>"
				+ "<br> <li> When working in 3D mode, make sure metadata (pixel size) are correctly calibrated, see Sequence Properties.</li> "
				+ "</html>","startmessage");
		addEzComponent(versioninfo);
//		prealign.setToolTipText("Volume can be turned in order to generate a new and still calibrated stack");
//		choiceinputsection.addVisibilityTriggerTo(prealign, "3D (X,Y,Z,[T])", "3D but let me update myself");
//		addEzComponent(prealign);

		//addComponent(new GuiCLEMButtonPreprocess());
		addComponent(new GuiCLEMButtonApply());
		addComponent(new advancedmodules(this));
		addEzComponent(inputGroup);

		choiceinputsection.setToolTipText("2D transform will be only in the plane XY " + "but can be applied to all dimensions.\n WARNING make sure to have the metadata correctly set in 3D");
		//choiceinputsection.addVisibilityTriggerTo(showgrid, "non rigid (2D or 3D)");

		guiCLEMButtons = new GuiCLEMButtons();
		guiCLEMButtons.disableButtons();
		addComponent(guiCLEMButtons);

		rigidspecificbutton = new GuiCLEMButtons2();
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

		boolean pause = false;
		source.setEnabled(false);
		target.setEnabled(false);
		choiceinputsection.setEnabled(false);
		showgrid.setEnabled(false);

		TransformationType transformationType = TransformationType.RIGID;
		if (choiceinputsection.getValue().equals(INPUT_SELECTION_2D)) {
			transformationType = TransformationType.RIGID;
			pause = false;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_2D_UPDATE)) {
			transformationType = TransformationType.RIGID;
			pause = true;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_3D)) {
			transformationType = TransformationType.RIGID;
			pause = false;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_3D_UPDATE)) {
			transformationType = TransformationType.RIGID;
			pause = true;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_NON_RIGID)) {
			transformationType = TransformationType.NON_RIGID;
			pause = true;
			rigidspecificbutton.removespecificrigidbutton();
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_AFFINE)) {
			transformationType = TransformationType.AFFINE;
			pause = false;
		}

		sourceSequence.setName(sourceSequence.getName() + " (transformed)");
		String name = sourceSequence.getFilename() + "_transfo.xml";

		workspace = new Workspace();
		workspace.setSourceSequence(sourceSequence);
		workspace.setTargetSequence(targetSequence);
		workspace.setSourceBackup(SequenceUtil.getCopy(sourceSequence));
		workspace.setXMLFile(new File(name));
		workspace.getWorkspaceState().setPause(pause);
		workspace.setTransformationConfiguration(transformationConfigurationFactory.getFrom(transformationType, showgrid.getValue()));

		guiCLEMButtons.setworkspace(workspace);
		rigidspecificbutton.setWorkspace(workspace);

//		RoiChanged roiChangedListener = new RoiChanged(workspace.getWorkspaceState(), workspaceTransformer);
//		sourceSequence.addListener(
//			roiChangedListener
//		);

		sourceSequence.addListener(
				new RoiDuplicator(targetSequence, workspace.getWorkspaceState())
		);

		targetSequence.addListener(
				new RoiDuplicator(sourceSequence, workspace.getWorkspaceState())
		);

		myoverlaysource = new VisiblepointsOverlay();
		myoverlaytarget = new VisiblepointsOverlay();

		messageSource = new MessageOverlay("SourceImage: will be transformed. Do not add point here but drag the points added from target");
		messageTarget = new MessageOverlay("Target Message: add Roi points here");
		sourceSequence.addOverlay(messageSource);
		targetSequence.addOverlay(messageTarget);
		sourceSequence.addOverlay(myoverlaysource);
		targetSequence.addOverlay(myoverlaytarget);

//		if (predictederrorselected) {
//			sourceSequence.addOverlay(myoverlaypredictederror);
//		}
//		if (overlayerrorselected) {
//			sourceSequence.addOverlay(myoverlayerror);
//		}

		sourceSequence.setFilename(sourceSequence.getName() + ".tif");
		new AnnounceFrame("Select point on image" + targetSequence.getName() + ", then drag it on source image and RIGHT CLICK", 5);

		guiCLEMButtons.enableButtons();
		rigidspecificbutton.enableButtons();

		currentThread = Thread.currentThread();
		currentThread.suspend();

//		sourceSequence.removeListener(roiChangedListener);
		sequenceListenerUtil.removeListeners(sourceSequence, RoiDuplicator.class);
		sequenceListenerUtil.removeListeners(targetSequence, RoiDuplicator.class);
		sourceSequence.removeOverlay(myoverlaysource);
		sourceSequence.removeOverlay(messageSource);
		targetSequence.removeOverlay(myoverlaytarget);
		targetSequence.removeOverlay(messageTarget);
//		sourceSequence.removeOverlay(myoverlayerror);
//		sourceSequence.removeOverlay(myoverlaypredictederror);
	}

	private void convertTo8Bit(Sequence sequence) {
		Sequence tmp;
		if (sequence.getDataType_().getBitSize() != 8) {
			tmp = SequenceUtil.convertToType(sequence, DataType.UBYTE, true);
			sequence.beginUpdate();
			sequence.removeAllImages();
			try {
				for (int t = 0; t < tmp.getSizeT(); t++) {
					for (int z = 0; z < tmp.getSizeZ(); z++) {
						IcyBufferedImage image = tmp.getImage(t, z);
						sequence.setImage(t, z, image);
					}
				}
			} finally {
				sequence.endUpdate();
			}
		}
		sequence.setAutoUpdateChannelBounds(true);
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
		rigidspecificbutton.reshowspecificrigidbutton();
		currentThread.resume();
	}
}
