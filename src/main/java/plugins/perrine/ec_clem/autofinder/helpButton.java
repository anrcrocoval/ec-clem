package plugins.perrine.ec_clem.autofinder;

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
 **/





import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import icy.plugin.PluginDescriptor;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;




public class helpButton extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public helpButton(EcClemAutoFinder ecclemAutoFinder) {
	JButton btnNewButton = new JButton("I need help");
	
	btnNewButton.setToolTipText("Press this button to launch a wizard to help you");
	
	btnNewButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Launching");
			for (final PluginDescriptor pluginDescriptor : PluginLoader
					.getPlugins()) {
				
				if (pluginDescriptor.getSimpleClassName()
						.compareToIgnoreCase("AutoFinderWizard") == 0) {
					// System.out.print(" ==> Starting by looking at the name....");

					// Create a new Runnable which contain the proper
					// launcher

					PluginLauncher.start(pluginDescriptor);
		
				}
		}
		}}
		
	);
	
	add(btnNewButton);
	}
}

