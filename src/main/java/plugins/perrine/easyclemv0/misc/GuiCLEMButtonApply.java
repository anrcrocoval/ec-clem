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

/**
 * Author: Perrine.Paul-Gilloteaux@curie.fr
 * one set of button: this one is to call the apply transform plugin 
 * and rename correctly the file in the Easyclem workflow
 */
package plugins.perrine.easyclemv0.misc;

import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GuiCLEMButtonApply extends JPanel {

	private static final long serialVersionUID = 1L;

	public GuiCLEMButtonApply() {
		JButton btnNewButton = new JButton("I want to apply a previously computed transfo");
		btnNewButton.setToolTipText("all transfo you were doing have been saved in a file named _transfo.storage");
		btnNewButton.addActionListener(arg0 -> PluginLauncher.start(PluginLoader.getPlugin(ApplyTransformation.class.getName())));
		add(btnNewButton);
	}
}
