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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import plugins.adufour.ezplug.EzButton;
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
import icy.plugin.PluginDescriptor;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.system.thread.ThreadUtil;
import icy.type.DataType;
import icy.type.point.Point5D;
import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.factory.TransformationConfigurationFactory;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.model.configuration.TransformationConfiguration;
import plugins.perrine.easyclemv0.roi.RoiProcessor;
import plugins.perrine.easyclemv0.sequence_listener.RoiAdded;

public class EasyCLEMv0 extends EzPlug implements EzStoppable {

	private Thread currentThread;
	private RoiProcessor roiProcessor = new RoiProcessor();
	private Workspace workspace;
	private WorkspaceTransformer workspaceTransformer;
	private TransformationConfigurationFactory transformationConfigurationFactory = new TransformationConfigurationFactory();

//	private ActionListener actionbutton = new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent arg0) {
//			PluginDescriptor plugin = PluginLoader.getPlugin(TransformBasedonCameraView.class.getName());
//			PluginLauncher.start(plugin);
//		}
//	};
//	private EzButton prealign = new EzButton("I want to prealign (rotate in 3D) my data)", actionbutton);

	private Overlay myoverlaysource;
	private Overlay myoverlaytarget;
	private Overlay messageSource;
	private Overlay messageTarget;
	private boolean nonrigid;

	static String[] listofRegistrationchoice = new String[] { "From Live to EM", "From Section to EM", "From Live to Section" };
	private EzVarBoolean showgrid = new EzVarBoolean(" Show grid deformation", false);

	private static String INPUT_SELECTION_2D = "2D (X,Y,[T])";
	private static String INPUT_SELECTION_2D_UPDATE = "2D but let me update myself";
	private static String INPUT_SELECTION_3D = "3D (X,Y,Z,[T])";
	private static String INPUT_SELECTION_3D_UPDATE = "3D but let me update myself";
	private static String INPUT_SELECTION_NON_RIGID = "non rigid (2D or 3D)";
	private EzVarText choiceinputsection = new EzVarText(
		"I want to compute the transformation in:",
			new String[] {
				INPUT_SELECTION_2D,
				INPUT_SELECTION_2D_UPDATE,
				INPUT_SELECTION_3D,
				INPUT_SELECTION_3D_UPDATE,
				INPUT_SELECTION_NON_RIGID
			}, 0, false
	);

	private EzLabel versioninfo = new EzLabel("Version " + this.getDescriptor().getVersion());
	private EzVarSequence target = new EzVarSequence("Select image that will not be modified (likely EM)");
	private EzVarSequence source = new EzVarSequence("Select image that will be transformed and resized (likely FM)");
	private EzGroup grp = new EzGroup("Images to process", source, target);
	private Sequence backupsource;
	private boolean mode3D = false;

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

	private boolean checkgrid = false;
	private GuiCLEMButtons guiCLEMButtons;
	private GuiCLEMButtons2 rigidspecificbutton;

//	public void setMonitoringConfiguration(MonitoringConfiguration monitoringConfiguration) {
//		this.monitoringConfiguration = monitoringConfiguration;
//	}

	/**
	 * Overlay surdefined for paint would happen at each paint: will make the
	 * points more visible.
	 * 
	 */
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

	/**
	 * Display an informative image on the top of sequences
	 * 
	 * @author Perrine
	 *
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.adufour.ezplug.EzPlug#initialize()
	 */
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
		addEzComponent(choiceinputsection);
		addEzComponent(showgrid);

//		prealign.setToolTipText("Volume can be turned in order to generate a new and still calibrated stack");
//		choiceinputsection.addVisibilityTriggerTo(prealign, "3D (X,Y,Z,[T])", "3D but let me update myself");
//		addEzComponent(prealign);

		addComponent(new GuiCLEMButtonPreprocess());
		addComponent(new GuiCLEMButtonApply());
		addComponent(new advancedmodules(this));
		addEzComponent(grp);
		choiceinputsection.setToolTipText("2D transform will be only in the plane XY " + "but can be applied to all dimensions.\n WARNING make sure to have the metadata correctly set in 3D");
		choiceinputsection.addVisibilityTriggerTo(showgrid, "non rigid (2D or 3D)");

		guiCLEMButtons = new GuiCLEMButtons();
		guiCLEMButtons.disableButtons();
		addComponent(guiCLEMButtons);

		rigidspecificbutton = new GuiCLEMButtons2();
		rigidspecificbutton.disableButtons();
		addComponent(rigidspecificbutton);
	}

	/**
	 * happening when pressing the play button: read the settings and launch the
	 * interactive mode of placing the points TODO: add the installation of the
	 * EDF easy plugin when needed
	 * 
	 */
	@Override
	protected void execute() {
		boolean pause = false;
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

		choiceinputsection.setEnabled(false);
		if (choiceinputsection.getValue().equals(INPUT_SELECTION_2D)) {
//			mode3D = false;
			nonrigid = false;
			pause = false;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_2D_UPDATE)) {
//			mode3D = false;
			nonrigid = false;
			pause = true;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_3D)) {
//			mode3D = true;
			nonrigid = false;
			pause = false;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_3D_UPDATE)) {
//			mode3D = true;
			nonrigid = false;
			pause = true;
		}

		if (choiceinputsection.getValue().equals(INPUT_SELECTION_NON_RIGID)) {
//			mode3D = false;
			nonrigid = true;
			pause = true;
			checkgrid = showgrid.getValue();
			rigidspecificbutton.removespecificrigidbutton();
		}

//		if (mode3D) {
//			new AnnounceFrame("Computation will be done in 3D, it can lead to instability in case of planar transformation", 5);
//			convertTo8Bit(sourceSequence);
//			convertTo8Bit(targetSequence);
//			new AnnounceFrame("Warning:" + sourceSequence.getName() + "and " + targetSequence.getName() + " have been converted to 8 bytes (to save memory in 3D)", 5);
//
//			if (sourceSequence.getSizeZ() == 1) {
//				sourceSequence.setPixelSizeZ(targetSequence.getPixelSizeZ());
//			}
//		}

		backupsource = SequenceUtil.getCopy(sourceSequence);
		sourceSequence.setName(sourceSequence.getName() + " (transformed)");
		String name = sourceSequence.getFilename() + "_transfo.xml";

		workspace = new Workspace();
		workspace.setSourceSequence(sourceSequence);
		workspace.setTargetSequence(targetSequence);
		workspace.setSourceBackup(backupsource);
		workspace.setXMLFile(new File(name));
		workspace.getWorkspaceState().setPause(pause);
		workspace.setTransformationConfiguration(transformationConfigurationFactory.getFrom(nonrigid, checkgrid));

		guiCLEMButtons.setworkspace(workspace);
		rigidspecificbutton.setWorkspace(workspace);

//		RoiChanged roiChangedListener = new RoiChanged(workspace.getWorkspaceState(), workspaceTransformer);
//		sourceSequence.addListener(
//			roiChangedListener
//		);

		RoiAdded roiAddedListener = new RoiAdded(sourceSequence, workspace.getWorkspaceState());
		targetSequence.addListener(
			roiAddedListener
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
		targetSequence.removeListener(roiAddedListener);
		System.out.println("Listeners off now");
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
		guiCLEMButtons.disableButtons();
		rigidspecificbutton.disableButtons();
		currentThread.resume();
		workspace.getWorkspaceState().setStopFlag(true);
//		try {
			choiceinputsection.setEnabled(true);
			rigidspecificbutton.reshowspecificrigidbutton();
			
			// Display the Total Final Transformation For Information
//			Document document = XMLUtil.loadDocument(XMLFile);
//			if ((mode3D==false)&&(nonrigid==false)){
//				Matrix combinedtransfobefore = getCombinedTransfo(document);
//				System.out.println("Here is transformation resulting from combined operation (between Play and Stop):");
//				combinedtransfobefore.print(3, 2);
//				double scale_x=Math.sqrt(Math.pow(combinedtransfobefore.get(0, 0),2)+Math.pow(combinedtransfobefore.get(0, 1),2));
//				//double scale_y=Math.sqrt(Math.pow(combinedtransfobefore.get(1, 0),2)+Math.pow(combinedtransfobefore.get(1, 1),2));
//				System.out.println("Estimated Scaling :" + (double)(Math.round(scale_x*100))/100);
//			}
//			if ((mode3D==true)&&(nonrigid==false)){
//				SimilarityTransformation3D combinedtransfobefore = getCombinedTransfo3D(document);
//				System.out.println("Here is transformation resulting from combined operation (between Play and Stop):");
//				combinedtransfobefore.getMatrix().print(3, 2);
//			}

//			if (sourceSequence != null)
//				sourceSequence.removeListener(this);
//			if (targetSequence != null)
//				targetSequence.removeListener(this);
//			ThreadUtil.invokeLater(new Runnable() {
//				public void run() {
//
//					if ((sourceSequence != null) && (targetSequence != null)) {
//
//						Sequence Result1 = SequenceUtil.extractSlice(sourceSequence, sourceSequence.getFirstViewer().getPositionZ());
//						Result1 = SequenceUtil.extractFrame(Result1, sourceSequence.getFirstViewer().getPositionT());
//						LUT sourcelut = sourceSequence.getFirstViewer().getLut();
//						int sourcenchannel = sourceSequence.getSizeC();
//						LUT targetlut = targetSequence.getFirstViewer().getLut();
//						int targetnchannel = targetSequence.getSizeC();
//
//						Sequence Result2 = null;
//						if (targetSequence.getSizeZ() >= sourceSequence.getSizeZ()) {
//							Result2 = SequenceUtil.extractSlice(targetSequence,
//									sourceSequence.getFirstViewer().getPositionZ());
//						} else {
//							Result2 = SequenceUtil.extractSlice(targetSequence,
//									targetSequence.getFirstViewer().getPositionZ());
//						}
//
//						Result2 = SequenceUtil.extractFrame(Result2, targetSequence.getFirstViewer().getPositionT());
//						Result2.dataChanged();
//						if (Result1.getDataType_() != Result2.getDataType_())
//							Result2 = SequenceUtil.convertToType(Result2, Result1.getDataType_(), true);
//						Result2.dataChanged();
//
//						Sequence[] sequences = new Sequence[Result1.getSizeC()+Result2.getSizeC()];
//						for (int c=0;c<Result1.getSizeC();c++)
//							sequences[c]=SequenceUtil.extractChannel(Result1, c);
//						for (int c=Result1.getSizeC();c<Result1.getSizeC()+Result2.getSizeC();c++)
//							sequences[c]=SequenceUtil.extractChannel(Result2, c-Result1.getSizeC());
//						boolean fillEmpty=false;
//						boolean rescale=false;
//						int[] channels=new int[sequences.length];
//						Sequence Result = SequenceUtil.concatC(sequences, channels, fillEmpty, rescale, null);
//
//						Viewer vout = new Viewer(Result);
//
//						Result.setName("Overlayed");
//						for (int c = 0; c < sourcenchannel; c++)
//							vout.getLut().getLutChannel(c).getColorMap()
//									.copyFrom(sourcelut.getLutChannel(c).getColorMap());
//
//						for (int c = 0; c < targetnchannel; c++)
//							vout.getLut().getLutChannel(sourcenchannel + c).getColorMap()
//									.copyFrom(targetlut.getLutChannel(c).getColorMap());
//
//						if (mode3D)// merge data and show source and target original with point on it.
//							new AnnounceFrame(
//									"Only the current z have been overlayed. Use the Merge Channel option if you want to create an overlay of the full stacks",
//									5);
//						else
//							new AnnounceFrame(
//									"The current views of both source and target image have been overlayed. Save it if you want to keep it. No further transform was done",
//									5);

//						sourceSequence.removeOverlay(myoverlaysource);
//						sourceSequence.removeOverlay(messageSource);
//						targetSequence.removeOverlay(myoverlaytarget);
//						targetSequence.removeOverlay(messageTarget);
//						sourceSequence.removeOverlay(myoverlayerror);
//						sourceSequence.removeOverlay(myoverlaypredictederror);
//					}

//				}

//			});
//		} catch (Exception e) {
//			System.out.println("byebye");
//		}
	}

	public RoiProcessor getRoiProcessor() {
		return roiProcessor;
	}
}
