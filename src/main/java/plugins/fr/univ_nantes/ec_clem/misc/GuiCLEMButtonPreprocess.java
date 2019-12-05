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

/**
 * Author: Perrine.Paul-Gilloteaux@curie.fr
 * one set of button: this one is to call the apply transform plugin 
 * and rename correctly the file in the Easyclem workflow
 */
package plugins.fr.univ_nantes.ec_clem.misc;

import plugins.fr.univ_nantes.ec_clem.ui.PreprocessButton;

import javax.swing.JPanel;

public class GuiCLEMButtonPreprocess extends JPanel {

	public GuiCLEMButtonPreprocess() {
		PreprocessButton btnNewButton = new PreprocessButton();
		add(btnNewButton);
	}
}