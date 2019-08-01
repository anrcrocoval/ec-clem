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

import plugins.perrine.easyclemv0.ui.PreprocessButton;
import javax.swing.JPanel;

public class GuiCLEMButtonPreprocess extends JPanel {

	private static final long serialVersionUID = 1L;

	public GuiCLEMButtonPreprocess() {
		PreprocessButton btnNewButton = new PreprocessButton();
		add(btnNewButton);
	}
}