package plugins.fr.univ_nantes.ec_clem.ec_clem.ui;

import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.RoiUpdater;
import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.Workspace;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowMeasuredErrorOverlaysCheckbox extends JCheckBox {

    private Workspace workspace;
    private RoiUpdater roiUpdater;

    @Inject
    public ShowMeasuredErrorOverlaysCheckbox(RoiUpdater roiUpdater) {
        super("Show measured error", false);
        this.roiUpdater = roiUpdater;
        addActionListener(this::action);
        setVisible(true);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action(ActionEvent arg0) {
        boolean selected = ((JCheckBox) arg0.getSource()).isSelected();
        workspace.getWorkspaceState().setShowMeasuredError(selected);
        roiUpdater.setLayersVisible(workspace.getSourceSequence(), PointType.MEASURED_ERROR, selected);
    }
}
