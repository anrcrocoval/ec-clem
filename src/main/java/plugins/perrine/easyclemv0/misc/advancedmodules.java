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
 * one set of button: this one is to call the apply transform plugin 
 * and rename correctly the file in the Easyclem workflow
 **/
package plugins.perrine.easyclemv0.misc;



import icy.plugin.PluginDescriptor;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.system.thread.ThreadUtil;
import plugins.perrine.easyclemv0.EasyCLEMv0;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import javax.swing.JPanel;

public class advancedmodules extends JPanel {

	private static final long serialVersionUID = 1L;
	EasyCLEMv0 matiteclasse;

	/**
	 * Create the panel.
	 */
	public advancedmodules(EasyCLEMv0 matiteclasse) {
		this.matiteclasse = matiteclasse;

		JButton btnNewButton = new JButton(
				"Advanced usage");

		btnNewButton
				.setToolTipText("Give access to more utilities.");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				
					for (final PluginDescriptor pluginDescriptor : PluginLoader
								.getPlugins()) {
							// System.out.print(pluginDescriptor.getSimpleClassName());
							// // output the name of the
							// class.

							// This part of the example check for a match in the
							// name of the class
							if (pluginDescriptor.getSimpleClassName()
									.compareToIgnoreCase("AdvancedEcClemOptions") == 0) {
								// System.out.print(" ==> Starting by looking at the name....");

								// Create a new Runnable which contain the proper
								// launcher
								ThreadUtil.invokeLater(new Runnable() {
									public void run() {
								PluginLauncher.start(pluginDescriptor);
					
									}});

							}
						}
					} 
			

	
			});
		add(btnNewButton);
		
		//add(tooltip);

	}
}
