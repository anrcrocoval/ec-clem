package plugins.perrine.easyclemv0.ui;


import plugins.perrine.easyclemv0.model.*;
import javax.swing.*;


public class ShowPointsButton extends JButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Workspace workspace;
    private WorkspaceTransformer workspaceTransformer = new WorkspaceTransformer();

    public ShowPointsButton() {
        super("Show ROIs on original source image");
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
