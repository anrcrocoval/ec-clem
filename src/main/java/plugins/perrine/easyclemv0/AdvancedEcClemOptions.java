/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux, CNRS.
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
package plugins.perrine.easyclemv0;

import icy.gui.frame.progress.ToolTipFrame;
import icy.plugin.PluginDescriptor;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.system.thread.ThreadUtil;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarText;

public class AdvancedEcClemOptions extends EzPlug  {
	
	EzVarText choiceplugin = new EzVarText("List of plugin utilities", new String[] {
			"AutoFinder (help me to find my cell from EM to LM)",
			"Apply a reduced scaled transform to a full size image",
			"Transform ROIs, not the images",
			"Import Roi from csv file (Amira or other)", "Create a protocol",
			"Study errors (leave one out vs predicted)",
			"Study errors (study influence of N and FLE)"}, 0, false);
	
	@Override
	public void clean() {}

	@Override
	protected void execute() {
		if (choiceplugin.getValue() == "AutoFinder (help me to find my cell from EM to LM)"){
			//launch autofinder
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("EcclemAutoFinder") == 0) {
					ThreadUtil.invokeLater(new Runnable() {
						public void run() {
					PluginLauncher.start(pluginDescriptor);
						}});
				}
			}
		}
		if (choiceplugin.getValue() == "Apply a reduced scaled transform to a full size image"){
			//launch scaled transform
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("ApplyTransfotoScaledImage") == 0) {
					ThreadUtil.invokeLater(new Runnable() {
						public void run() {
					PluginLauncher.start(pluginDescriptor);
						}});
				}
			}
		}
		if (choiceplugin.getValue() == "Transform ROIs, not the images"){
			//launch ROI transforms
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("ApplyTransformationtoRoi") == 0) {
					ThreadUtil.invokeLater(new Runnable() {
						public void run() {
					PluginLauncher.start(pluginDescriptor);
						}});
				}
			}
		}
		if (choiceplugin.getValue() == "Study errors (leave one out vs predicted)"){
			//launch autofinder
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("MonteCarloTREstudy_Validation") == 0) {
					ThreadUtil.invokeLater(new Runnable() {
						public void run() {
					PluginLauncher.start(pluginDescriptor);
						}});
				}
			}
		}
		if (choiceplugin.getValue() == "Study errors (study influence of N and FLE)"){
			//launch autofinder
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("StudyLandmarksConfagainstN") == 0) {
					ThreadUtil.invokeLater(new Runnable() {
						public void run() {
					PluginLauncher.start(pluginDescriptor);
						}});
				}
			}
		}
		if (choiceplugin.getValue() == "Import Roi from csv file (Amira or other)"){
			//launch autofinder
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("ImportRoiPointsFromFile") == 0) {
					ThreadUtil.invokeLater(new Runnable() {
						public void run() {
					PluginLauncher.start(pluginDescriptor);
						}});
				}
			}
		}
		
		if (choiceplugin.getValue() == "Create a protocol"){
			new ToolTipFrame(    			
	    			"<html>"+
	    			"<br>You can use the Icy feature of visual programming: "+
	    			"<br> <b>Protocols</b> to apply any transform," +
	    			"<br>computed in EC-Clem (2D, 3D, or non rigid) by using the"+
	    			"<br> <b>ApplyTransformation</b> block in your own protocol."+
	    			
	    			"</html>"
	    			);
		 
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("Protocols") == 0) {
					
					PluginLauncher.start(pluginDescriptor);

				}
			}
		}
	}

	@Override
	protected void initialize() {
		EzLabel textinfo=new EzLabel("Here is a list of additional features you may find useful.");
		addEzComponent(textinfo);
		addEzComponent(choiceplugin);
	}
}