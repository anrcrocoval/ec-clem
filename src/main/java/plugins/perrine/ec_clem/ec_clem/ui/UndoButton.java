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

import plugins.perrine.ec_clem.ec_clem.workspace.Undo;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;

import javax.inject.Inject;
import javax.swing.*;
import java.util.concurrent.CompletableFuture;

public class UndoButton extends JButton {

	private Workspace workspace;
    private ProgressBarManager progressBarManager;

    @Inject
    public UndoButton(ProgressBarManager progressBarManager) {
        super("Undo last point");
        this.progressBarManager = progressBarManager;
        setToolTipText("Press this button to cancel the last point edition you have done, it will reverse to the previous state of your image");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        Undo undo = new Undo(workspace);
        progressBarManager.subscribe(undo);
        CompletableFuture.runAsync(undo);
    }
}
