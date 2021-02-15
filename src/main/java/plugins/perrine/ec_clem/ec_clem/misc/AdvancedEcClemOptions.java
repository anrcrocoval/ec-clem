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
package plugins.perrine.ec_clem.ec_clem.misc;

import icy.gui.frame.progress.ToolTipFrame;
import icy.plugin.PluginDescriptor;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.system.thread.ThreadUtil;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarText;
import plugins.perrine.ec_clem.cascade_transform.EcClemCascadeTransform;
import plugins.perrine.ec_clem.error.EcClemError;
import plugins.perrine.ec_clem.invert_transformation_schema.EcClemTransformationSchemaInverter;
import plugins.perrine.ec_clem.transform.EcClemTransform;
import plugins.perrine.ec_clem.transformation_schema_loader.EcClemTransformationSchemaLoader;

public class AdvancedEcClemOptions extends EzPlug  {
//	private final String AUTOFINDER = "AutoFinder (help me to find my cell from EM to LM)";
	private final String APPLY_TRANSFORMATION = "Apply a reduced scaled transform to a full size image";
	private final String TRANSFORM_ROI = "Transform ROIs, not the images";
	private final String IMPORT_ROI = "Import Roi from csv file (Amira or other)";
//	private final String PROTOCOL = "Create a protocol";
//	private final String ERROR_LOO = "Study errors (leave one out vs predicted)";
//	private final String ERROR_FLE = "Study errors (study influence of N and FLE)";

	private final String LOAD = "Load transformation schema";
	private final String APPLY_TRANSFORMATION_SCHEMA = "Apply transformation schema";
	private final String INVERT = "Invert transformation schema";
	private final String CASCADE = "Compute cascading transformation schema";
	private final String ERROR_MAP = "Compute error map";

	EzVarText choiceplugin = new EzVarText("List of plugin utilities", new String[] {
//		AUTOFINDER,
//		APPLY,
//		TRANSFORM_ROI,
//		IMPORT_ROI,
//		PROTOCOL,
//		ERROR_LOO,
//		ERROR_FLE,
			APPLY_TRANSFORMATION,
			TRANSFORM_ROI,
			IMPORT_ROI,
			LOAD,
			APPLY_TRANSFORMATION_SCHEMA,
			INVERT,
			CASCADE,
			ERROR_MAP
	}, 0, false);
	
	@Override
	public void clean() {}

	@Override
	protected void execute() {
		String pluginClassName;
		switch (choiceplugin.getValue()) {
			case APPLY_TRANSFORMATION: {
				pluginClassName = ApplyTransformation.class.getName();
				break;
			}
			case TRANSFORM_ROI: {
				pluginClassName = ApplyTransformationtoRoi.class.getName();
				break;
			}
			case IMPORT_ROI: {
				pluginClassName = ImportRoiPointsFromFile.class.getName();
				break;
			}
			case LOAD: {
				pluginClassName = EcClemTransformationSchemaLoader.class.getName();
				break;
			}
			case APPLY_TRANSFORMATION_SCHEMA: {
				pluginClassName = EcClemTransform.class.getName();
				break;
			}
			case INVERT: {
				pluginClassName = EcClemTransformationSchemaInverter.class.getName();
				break;
			}
			case CASCADE: {
				pluginClassName = EcClemCascadeTransform.class.getName();
				break;
			}
			case ERROR_MAP: {
				pluginClassName = EcClemError.class.getName();
				break;
			}
			default: throw new RuntimeException("Not supported");
		}
		ThreadUtil.invokeLater(() -> PluginLauncher.start(PluginLoader.getPlugin(pluginClassName)));
//		if (choiceplugin.getValue().equals(AUTOFINDER)) {
//			PluginLauncher.start(PluginLoader.getPlugin("EcclemAutoFinder"));
//			for (final PluginDescriptor pluginDescriptor : PluginLoader.getPlugins()) {
//				if (pluginDescriptor.getSimpleClassName().compareToIgnoreCase("EcclemAutoFinder") == 0) {
//					ThreadUtil.invokeLater(() -> PluginLauncher.start(pluginDescriptor));
//				}
//			}
//		}

//		if (choiceplugin.getValue().equals(APPLY)) {
//			PluginLauncher.start(PluginLoader.getPlugin("ApplyTransfotoScaledImage"));
//			for (final PluginDescriptor pluginDescriptor : PluginLoader.getPlugins()) {
//				if (pluginDescriptor.getSimpleClassName().compareToIgnoreCase("ApplyTransfotoScaledImage") == 0) {
//					ThreadUtil.invokeLater(() -> PluginLauncher.start(pluginDescriptor));
//				}
//			}
//		}

//		if (choiceplugin.getValue().equals(TRANSFORM_ROI)){
//			PluginLauncher.start(PluginLoader.getPlugin("ApplyTransformationtoRoi"));
//			for (final PluginDescriptor pluginDescriptor : PluginLoader.getPlugins()) {
//				if (pluginDescriptor.getSimpleClassName().compareToIgnoreCase("ApplyTransformationtoRoi") == 0) {
//					ThreadUtil.invokeLater(() -> PluginLauncher.start(pluginDescriptor));
//				}
//			}
//		}

//		if (choiceplugin.getValue().equals(IMPORT_ROI)) {
//			PluginLauncher.start(PluginLoader.getPlugin("ImportRoiPointsFromFile"));
//			for (final PluginDescriptor pluginDescriptor : PluginLoader.getPlugins()) {
//				if (pluginDescriptor.getSimpleClassName().compareToIgnoreCase("ImportRoiPointsFromFile") == 0) {
//					ThreadUtil.invokeLater(() -> PluginLauncher.start(pluginDescriptor));
//				}
//			}
//		}

//		if (choiceplugin.getValue().equals(PROTOCOL)) {
//			new ToolTipFrame(
//				"<html>" +
//				"<br>You can use the Icy feature of visual programming: " +
//				"<br> <b>Protocols</b> to apply any transform," +
//				"<br>computed in EC-Clem (2D, 3D, or non rigid) by using the" +
//				"<br> <b>ApplyTransformation</b> block in your own protocol." +
//				"</html>"
//			);
//
//			PluginLauncher.start(PluginLoader.getPlugin(plugins.adufour.protocols.Protocols.class.getName()));

//			for (final PluginDescriptor pluginDescriptor : PluginLoader.getPlugins()) {
//				if (pluginDescriptor.getSimpleClassName().compareToIgnoreCase("Protocols") == 0) {
//					PluginLauncher.start(pluginDescriptor);
//				}
//			}
//		}

//		if (choiceplugin.getValue().equals(ERROR_LOO)) {
//			PluginLauncher.start(PluginLoader.getPlugin("MonteCarloTREstudy_Validation"));
//			for (final PluginDescriptor pluginDescriptor : PluginLoader.getPlugins()) {
//				if (pluginDescriptor.getSimpleClassName().compareToIgnoreCase("MonteCarloTREstudy_Validation") == 0) {
//					ThreadUtil.invokeLater(() -> PluginLauncher.start(pluginDescriptor));
//				}
//			}
//		}

//		if (choiceplugin.getValue().equals(ERROR_FLE)) {
//			PluginLauncher.start(PluginLoader.getPlugin("StudyLandmarksConfagainstN"));
//			for (final PluginDescriptor pluginDescriptor : PluginLoader.getPlugins()) {
//				if (pluginDescriptor.getSimpleClassName().compareToIgnoreCase("StudyLandmarksConfagainstN") == 0) {
//					ThreadUtil.invokeLater(() -> PluginLauncher.start(pluginDescriptor));
//				}
//			}
//		}
	}

	@Override
	protected void initialize() {
		EzLabel textinfo = new EzLabel("Here is a list of additional features you may find useful.");
		addEzComponent(textinfo);
		addEzComponent(choiceplugin);
	}
}