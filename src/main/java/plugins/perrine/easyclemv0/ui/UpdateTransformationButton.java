package plugins.perrine.easyclemv0.ui;

import plugins.perrine.easyclemv0.workspace.Workspace;
import plugins.perrine.easyclemv0.workspace.WorkspaceTransformer;
import javax.inject.Inject;
import javax.swing.*;

public class UpdateTransformationButton extends JButton {

	private static final long serialVersionUID = 1L; //to avoid warning
    private WorkspaceTransformer workspaceTransformer;
    private Workspace workspace;

    @Inject
    public UpdateTransformationButton(WorkspaceTransformer workspaceTransformer) {
        super("Update TransformationSchema");
        this.workspaceTransformer = workspaceTransformer;
        setToolTipText("Press this button if you have moved the points, prepared set of points, \n or obtained some black part of the image. This will refresh it");
        addActionListener((arg0) -> action());
    }

    private void action() {
        workspaceTransformer.apply(workspace);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
