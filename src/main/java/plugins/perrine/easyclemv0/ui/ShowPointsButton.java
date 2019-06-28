package plugins.perrine.easyclemv0.ui;

import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.model.*;
import javax.swing.*;
import java.util.List;

public class ShowPointsButton extends JButton {

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

    private void saveRois(Sequence sequence) {
        final List<ROI> rois = sequence.getROIs();
        if (rois.size() > 0) {
            final Document doc = XMLUtil.createDocument(true);
            if (doc != null) {
                ROI.saveROIsToXML(XMLUtil.getRootElement(doc), rois);
                System.out.println("ROIS saved before in "+ sequence.getFilename()+"_ROIsavedwhenshowonoriginaldata.xml"+"\n Use Load Roi(s) if needed in ROI top menu" );
                XMLUtil.saveDocument(doc, sequence.getFilename()+"_ROIsavedwhenshowonoriginaldata.xml");
            }
        }
    }
}
