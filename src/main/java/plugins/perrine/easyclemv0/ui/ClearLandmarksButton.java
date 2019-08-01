package plugins.perrine.easyclemv0.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.workspace.Workspace;

import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

public class ClearLandmarksButton extends JButton {

    private Workspace workspace;

    @Inject
    public ClearLandmarksButton() {
        super("Clear all landmarks points");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        if (workspace.getSourceSequence() == null || workspace.getTargetSequence() == null) {
            MessageDialog.showDialog("Make sure source and target image are openned and selected");
            return;
        }
        clearLandmarks(workspace.getSourceSequence());
        clearLandmarks(workspace.getTargetSequence());
        new AnnounceFrame("All ROIs have been deleted from images " + workspace.getSourceSequence().getName() + " and " + workspace.getTargetSequence().getName(),5);
    }

    private void clearLandmarks(Sequence sequence) {
        saveROI(sequence);
        sequence.removeAllROI(true);
    }

    private void saveROI(Sequence sequence) {
        final List<ROI> rois = sequence.getROIs();
        if (rois.size() > 0) {
            final Document doc = XMLUtil.createDocument(true);
            if (doc != null) {
                ROI.saveROIsToXML(XMLUtil.getRootElement(doc), rois);
                System.out.println("ROIS saved before in "+ sequence.getFilename()+"_ROIsavedBeforeClearLandmarks.xml"+"\n Use Load Roi(s) if needed in ROI top menu" );
                XMLUtil.saveDocument(doc, sequence.getFilename()+"_ROIsavedBeforeClearLandmarks.xml");

            }
        }
    }
}
