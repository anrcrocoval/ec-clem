package plugins.fr.univ_nantes.ec_clem.ec_clem.ui;

import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence_listener.RoiListenerManager;
import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.Workspace;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SelectPointTypeBox extends JPanel {

    private JLabel jLabel;
    private JComboBox<PointType> stringJComboBox;
    private Workspace workspace;

    private RoiListenerManager roiListenerManager;

    @Inject
    public SelectPointTypeBox(RoiListenerManager roiListenerManager) {
        this.roiListenerManager = roiListenerManager;
        jLabel = new JLabel("Point type");
        stringJComboBox = new JComboBox<>(new PointType[] { PointType.FIDUCIAL, PointType.NOT_FIDUCIAL});
        stringJComboBox.addItemListener(itemEvent -> {
            if(itemEvent.getStateChange() == ItemEvent.SELECTED) {
                roiListenerManager.set((PointType) itemEvent.getItem());
            }
        });
        add(jLabel);
        add(stringJComboBox);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        roiListenerManager.setSequences(workspace.getSourceSequence(), workspace.getTargetSequence());
        stringJComboBox.setSelectedItem(PointType.FIDUCIAL);
        roiListenerManager.set(PointType.FIDUCIAL);
    }

    public void setEnabled(boolean bool) {
        for(Component component : getComponents()) {
            component.setEnabled(bool);
        }
    }
}
