package plugins.perrine.easyclemv0.ui;

import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import plugins.perrine.easyclemv0.misc.Preprocess3Dstackto2D;

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
