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
package plugins.fr.univ_nantes.ec_clem.ec_clem.ui;

import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.Workspace;
import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.WorkspaceTransformer;
import icy.gui.dialog.MessageDialog;

import javax.inject.Inject;
import javax.swing.*;
import java.util.concurrent.CompletableFuture;

public class UpdateTransformationButton extends JButton {

    private ProgressBarManager progressBarManager;
    private Workspace workspace;

    @Inject
    public UpdateTransformationButton(ProgressBarManager progressBarManager) {
        super("Update Transformation");
        this.progressBarManager = progressBarManager;
        setToolTipText("Press this button if you have moved the points, prepared set of points, \n or obtained some black part of the image. This will refresh it");
        addActionListener((arg0) -> action());
    }

    private void action() {
        WorkspaceTransformer workspaceTransformer = new WorkspaceTransformer(workspace);
        progressBarManager.subscribe(workspaceTransformer);
        CompletableFuture.runAsync(workspaceTransformer).exceptionally(e -> {
            MessageDialog.showDialog("Something went wrong: "+e.getCause().getMessage(), MessageDialog.ERROR_MESSAGE);
            return null;
        });
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
