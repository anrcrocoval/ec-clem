package plugins.perrine.ec_clem.ec_clem.ui;

import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.roi.RoiUpdater;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowPredictedErrorOverlaysCheckbox extends JCheckBox {

    private Workspace workspace;
    private RoiUpdater roiUpdater;

    @Inject
    public ShowPredictedErrorOverlaysCheckbox(RoiUpdater roiUpdater) {
        super("Show predicted error", false);
        this.roiUpdater = roiUpdater;
        addActionListener(this::action);
        setVisible(true);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action(ActionEvent arg0) {
        boolean selected = ((JCheckBox) arg0.getSource()).isSelected();
        workspace.getWorkspaceState().setShowPredictedError(selected);
        roiUpdater.setLayersVisible(workspace.getSourceSequence(), PointType.PREDICTED_ERROR, selected);
    }
}
