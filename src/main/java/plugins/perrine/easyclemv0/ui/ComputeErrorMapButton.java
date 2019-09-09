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

import icy.gui.dialog.MessageDialog;
import icy.gui.viewer.Viewer;
import icy.image.colormap.FireColorMap;
import icy.system.thread.ThreadUtil;
import plugins.perrine.easyclemv0.error.fitzpatrick.TargetRegistrationErrorMapFactory;
import plugins.perrine.easyclemv0.error.fitzpatrick.TargetRegistrationErrorMapSupplier;
import plugins.perrine.easyclemv0.workspace.Workspace;
import javax.inject.Inject;
import javax.swing.*;
import java.util.concurrent.CompletableFuture;

public class ComputeErrorMapButton extends JButton {

	private static final long serialVersionUID = 1L;
	private Workspace workspace;
    private TargetRegistrationErrorMapFactory targetRegistrationErrorMapFactory;
    private ProgressBarManager progressBarManager;

    @Inject
    public ComputeErrorMapButton(
            TargetRegistrationErrorMapFactory targetRegistrationErrorMapFactory,
            ProgressBarManager progressBarManager
    ) {
        super("Compute the whole predicted error map ");
        this.targetRegistrationErrorMapFactory = targetRegistrationErrorMapFactory;
        this.progressBarManager = progressBarManager;
        setToolTipText(" This will compute a new image were each pixel value stands for the statistical registration error (called Target Registration Error");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        try {
            TargetRegistrationErrorMapSupplier targetRegistrationErrorMapSupplier = targetRegistrationErrorMapFactory.getFrom(workspace);
            progressBarManager.subscribe(targetRegistrationErrorMapSupplier);
            CompletableFuture
                .supplyAsync(targetRegistrationErrorMapSupplier)
                .thenAccept(sequence -> ThreadUtil.invokeLater(() -> {
                    Viewer viewer = new Viewer(sequence);
                    viewer.getLut()
                        .getLutChannel(0)
                        .setColorMap(new FireColorMap(), false);
                }));
        } catch(RuntimeException e) {
            e.printStackTrace();
            MessageDialog.showDialog(e.getMessage(), MessageDialog.ERROR_MESSAGE);
        }
    }
}