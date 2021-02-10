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
package plugins.perrine.easyclemv0.ec_clem.ui;

import plugins.perrine.easyclemv0.ec_clem.workspace.ResetOriginalImage;
import plugins.perrine.easyclemv0.ec_clem.workspace.Workspace;

import javax.inject.Inject;
import javax.swing.*;
import java.util.concurrent.CompletableFuture;

public class ShowPointsButton extends JButton {

    private Workspace workspace;
    private ProgressBarManager progressBarManager;

    @Inject
    public ShowPointsButton(ProgressBarManager progressBarManager) {
        super("Show ROIs on original source image");
        this.progressBarManager = progressBarManager;
        setToolTipText("Show the original source Image, with the points selected shown (save the source image to save the ROIs)");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        ResetOriginalImage resetOriginalImage = new ResetOriginalImage(workspace);
        progressBarManager.subscribe(resetOriginalImage);
        CompletableFuture.runAsync(resetOriginalImage);
    }
}
