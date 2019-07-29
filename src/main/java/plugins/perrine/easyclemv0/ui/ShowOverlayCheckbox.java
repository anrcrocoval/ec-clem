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

import icy.painter.Overlay;
import plugins.perrine.easyclemv0.workspace.Workspace;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowOverlayCheckbox extends JCheckBox {

	private static final long serialVersionUID = 1L;
	private Workspace workspace;
    private Overlay overlay;

    public ShowOverlayCheckbox(Overlay overlay, String text, String tooltip) {
        super(text, false);
        this.overlay = overlay;
        setToolTipText(tooltip);
        addActionListener((arg0) -> action(arg0));
        setVisible(true);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
    }

    private void action(ActionEvent arg0) {
        if (((JCheckBox) arg0.getSource()).isSelected()) {
            workspace.getSourceSequence().addOverlay(overlay);
        } else {
            workspace.getSourceSequence().removeOverlay(overlay);
        }
    }
}
