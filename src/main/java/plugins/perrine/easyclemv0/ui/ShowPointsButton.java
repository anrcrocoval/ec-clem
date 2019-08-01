package plugins.perrine.easyclemv0.ui;

import plugins.perrine.easyclemv0.workspace.Workspace;
import plugins.perrine.easyclemv0.workspace.WorkspaceTransformer;
import javax.inject.Inject;
import javax.swing.*;

public class ShowPointsButton extends JButton {

	private static final long serialVersionUID = 1L;
    private Workspace workspace;
    private WorkspaceTransformer workspaceTransformer;

    @Inject
    public ShowPointsButton(WorkspaceTransformer workspaceTransformer) {
        super("Show ROIs on original source image");
        this.workspaceTransformer = workspaceTransformer;
        setToolTipText("Show the original source Image, with the points selected shown (save the source image to save the ROIs)");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        workspaceTransformer.resetToOriginalImage(workspace);
    }
}
