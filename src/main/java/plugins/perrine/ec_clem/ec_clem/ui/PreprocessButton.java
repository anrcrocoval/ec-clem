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
package plugins.perrine.ec_clem.ec_clem.ui;

import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import plugins.perrine.ec_clem.ec_clem.misc.Preprocess3Dstackto2D;
import javax.inject.Inject;
import javax.swing.*;

public class PreprocessButton extends JButton {

    @Inject
    public PreprocessButton() {
        super("I want to preprocess my data");
        setToolTipText("Do it before computing the transform: it can help by reducing the dimensionality with flattening for exemple. You will then still be able to apply the transformation computed to the full stack/movie in a second run.");
        addActionListener((arg0) -> action());
    }

    private void action() {
        PluginLauncher.start(PluginLoader.getPlugin(Preprocess3Dstackto2D.class.getName()));
    }
}
