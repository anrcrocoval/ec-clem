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
 * one second set of button: this one is to call the update transformation
 *  and the clear landmarks methods
 */
package plugins.perrine.easyclemv0.ui;

import javax.swing.JPanel;
import plugins.perrine.easyclemv0.model.Workspace;
import plugins.perrine.easyclemv0.model.WorkspaceTransformer;
import plugins.perrine.easyclemv0.ui.ClearLandmarksButton;
import plugins.perrine.easyclemv0.ui.ShowPointsButton;
import plugins.perrine.easyclemv0.ui.UndoButton;
import plugins.perrine.easyclemv0.ui.UpdateTransformationButton;

public class GuiCLEMButtons extends JPanel {

    private static final long serialVersionUID = 1L;

    private UpdateTransformationButton updateTransformationButton = new UpdateTransformationButton();
    private ClearLandmarksButton clearLandmarksButton = new ClearLandmarksButton();
    private UndoButton undoButton = new UndoButton();
    private ShowPointsButton showPointsButton = new ShowPointsButton();

    public GuiCLEMButtons() {
        add(updateTransformationButton);
        add(clearLandmarksButton);
        add(undoButton);
        add(showPointsButton);
    }

    public void setworkspace(Workspace workspace) {
        updateTransformationButton.setWorkspace(workspace);
        clearLandmarksButton.setWorkspace(workspace);
        undoButton.setWorkspace(workspace);
        showPointsButton.setWorkspace(workspace);
    }

    public void disableButtons() {
        updateTransformationButton.setEnabled(false);
        clearLandmarksButton.setEnabled(false);
        undoButton.setEnabled(false);
        showPointsButton.setEnabled(false);
    }

    public void enableButtons() {
        updateTransformationButton.setEnabled(true);
        clearLandmarksButton.setEnabled(true);
        undoButton.setEnabled(true);
        showPointsButton.setEnabled(true);
    }
}