package plugins.perrine.easyclemv0.ui;

import icy.painter.Overlay;
import plugins.perrine.easyclemv0.model.Workspace;
import plugins.perrine.easyclemv0.overlay.ErrorInPositionOverlay;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowOverlayCheckbox extends JCheckBox {

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
