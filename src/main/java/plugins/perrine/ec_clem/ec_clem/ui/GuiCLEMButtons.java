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
package plugins.perrine.ec_clem.ec_clem.ui;

import javax.inject.Inject;
import javax.swing.JPanel;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;

import java.awt.*;

public class GuiCLEMButtons extends JPanel {

    private SelectPointTypeBox selectPointTypeBox;
    private UpdateTransformationButton updateTransformationButton;
    private ClearLandmarksButton clearLandmarksButton;
    private UndoButton undoButton;
    private ShowPointsButton showPointsButton;
    private MergeButton mergeButton;

    @Inject
    public GuiCLEMButtons(
        UpdateTransformationButton updateTransformationButton,
        ClearLandmarksButton clearLandmarksButton,
        UndoButton undoButton,
        ShowPointsButton showPointsButton,
        SelectPointTypeBox selectPointTypeBox,
        MergeButton mergeButton
    ) {
        this.selectPointTypeBox = selectPointTypeBox;
        this.updateTransformationButton = updateTransformationButton;
        this.clearLandmarksButton = clearLandmarksButton;
        this.undoButton = undoButton;
        this.showPointsButton = showPointsButton;
        this.mergeButton = mergeButton;
        add(selectPointTypeBox);
        add(updateTransformationButton);
        add(clearLandmarksButton);
        add(undoButton);
        add(showPointsButton);
        add(mergeButton);
    }

    public void setworkspace(Workspace workspace) {
        updateTransformationButton.setWorkspace(workspace);
        clearLandmarksButton.setWorkspace(workspace);
        undoButton.setWorkspace(workspace);
        showPointsButton.setWorkspace(workspace);
        selectPointTypeBox.setWorkspace(workspace);
        mergeButton.setWorkspace(workspace);
    }

    public void setEnabled(boolean bool) {
        for(Component component : getComponents()) {
            component.setEnabled(bool);
        }
    }
}