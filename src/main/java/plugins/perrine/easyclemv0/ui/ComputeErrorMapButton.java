package plugins.perrine.easyclemv0.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import plugins.perrine.easyclemv0.error.TargetRegistrationErrorMap;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.error.TREComputerFactory;
import plugins.perrine.easyclemv0.workspace.Workspace;

import javax.inject.Inject;
import javax.swing.*;

public class ComputeErrorMapButton extends JButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Workspace workspace;
    private TREComputer treComputer;
    private TREComputerFactory treComputerFactory;
    private TargetRegistrationErrorMap targetRegistrationErrorMap;

    @Inject
    public ComputeErrorMapButton(TREComputerFactory treComputerFactory, TargetRegistrationErrorMap targetRegistrationErrorMap) {
        super("Compute the whole predicted error map ");
        this.treComputerFactory = treComputerFactory;
        this.targetRegistrationErrorMap = targetRegistrationErrorMap;
        setToolTipText(" This will compute a new image were each pixel value stands for the statistical registration error (called Target Registration Error");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        treComputer = treComputerFactory.getFrom(workspace);
        targetRegistrationErrorMap.setTreComputer(treComputer);
        if (workspace.getSourceSequence() != null) {
            if (workspace.getSourceSequence().getROIs().size() < 3) {
                new AnnounceFrame("Without at least 3 ROI points, the error map does not have any meaning. Please add points.",5);
            } else {
//						double fle = fleComputer.maxdifferrorinnm(
//							datasetFactory.getFrom(matiteclasse.source.getValue()),
//							datasetFactory.getFrom(matiteclasse.target.getValue()),
//							matiteclasse.source.getValue().getPixelSizeX(),
//							matiteclasse.target.getValue().getPixelSizeX()
//						);
//						if (fle == 0){
//							MessageDialog.showDialog("Please Initialize EC-Clem first by pressing the Play button");
//							return;
//						}
                targetRegistrationErrorMap.apply(workspace.getTargetSequence(), workspace.getTargetSequence().getFirstImage());
            }
        } else {
            MessageDialog.showDialog("Source and target were closed. Please open one of them and try again");
        }
    }
}
