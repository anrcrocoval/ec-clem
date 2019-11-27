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
package plugins.fr.univ_nantes.ec_clem.misc;



import plugins.fr.univ_nantes.ec_clem.EasyCLEMv0;
import icy.plugin.PluginDescriptor;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.system.thread.ThreadUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import javax.swing.JPanel;

public class advancedmodules extends JPanel {

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
