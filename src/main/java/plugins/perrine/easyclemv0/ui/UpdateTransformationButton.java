package plugins.perrine.easyclemv0.ui;


import plugins.perrine.easyclemv0.model.Workspace;
import plugins.perrine.easyclemv0.model.WorkspaceTransformer;
import javax.swing.*;

public class UpdateTransformationButton extends JButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; //to avoid warning
	private WorkspaceTransformer workspaceTransformer = new WorkspaceTransformer();
    private Workspace workspace;

    public UpdateTransformationButton() {
        super("Update Transformation");
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
