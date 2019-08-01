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
package plugins.perrine.easyclemv0.ui;

import icy.plugin.PluginDescriptor;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import plugins.perrine.easyclemv0.workspace.Workspace;
import plugins.perrine.easyclemv0.monitor.MonitorTargetOverlay;
import plugins.perrine.easyclemv0.monitor.MonitorTargetPoint;

import javax.inject.Inject;
import javax.swing.*;

public class MonitorTargetPointButton extends JButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Workspace workspace;

    @Inject
    public MonitorTargetPointButton() {
        super("Monitor a target point ");
        setToolTipText(" This will display the evolution of the target registration error at one target position while points are added");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        PluginDescriptor plugin = PluginLoader.getPlugin(MonitorTargetPoint.class.getName());
        MonitorTargetOverlay monitorTargetOverlay = new MonitorTargetOverlay(
            workspace.getMonitoringConfiguration()
        );
        workspace.getTargetSequence().addOverlay(monitorTargetOverlay);
        PluginLauncher.start(plugin);
    }
}
