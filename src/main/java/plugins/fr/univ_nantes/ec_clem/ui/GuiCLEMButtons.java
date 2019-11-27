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
 * one second set of button: this one is to call the update transformation
 *  and the clear landmarks methods
 */
package plugins.fr.univ_nantes.ec_clem.ui;

import javax.inject.Inject;
import javax.swing.JPanel;
import plugins.fr.univ_nantes.ec_clem.workspace.Workspace;

public class GuiCLEMButtons extends JPanel {

    private static final long serialVersionUID = 1L;
    private UpdateTransformationButton updateTransformationButton;
    private ClearLandmarksButton clearLandmarksButton;
    private UndoButton undoButton;
    private ShowPointsButton showPointsButton;

    @Inject
    public GuiCLEMButtons(UpdateTransformationButton updateTransformationButton, ClearLandmarksButton clearLandmarksButton, UndoButton undoButton, ShowPointsButton showPointsButton) {
        this.updateTransformationButton = updateTransformationButton;
        this.clearLandmarksButton = clearLandmarksButton;
        this.undoButton = undoButton;
        this.showPointsButton = showPointsButton;
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