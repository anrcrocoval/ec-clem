package plugins.perrine.easyclemv0.ui;

import icy.roi.ROI;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.SequenceSizeFactory;
import plugins.perrine.easyclemv0.model.Workspace;
import plugins.perrine.easyclemv0.model.WorkspaceTransformer;
import plugins.perrine.easyclemv0.storage.xml.XmlFileReader;
import plugins.perrine.easyclemv0.storage.xml.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.xml.XmlTransformationWriter;
import javax.swing.*;
import java.util.ArrayList;

public class UpdateTransformationButton extends JButton {

    private WorkspaceTransformer workspaceTransformer = new WorkspaceTransformer();
    private Workspace workspace;
    private XmlFileWriter xmlFileWriter;
    private XmlTransformationWriter xmlWriter;
    private XmlFileReader xmlFileReader = new XmlFileReader();
    private SequenceSizeFactory sequenceSizeFactory = new SequenceSizeFactory();

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
//        Document document = xmlFileReader.loadFile(workspace.getXMLFile());
//        xmlFileWriter = new XmlFileWriter(document, workspace.getXMLFile());
//        xmlWriter = new XmlTransformationWriter(document);
//        sequenceSizeXmlWriter.writeSizeOf(sequenceSizeFactory.getFrom(workspace.getTargetSequence()), );
//        xmlFileWriter.write();
    }

    private boolean checkRoiNames(Sequence sequence) {
        boolean removed = false;
        ArrayList<ROI> listroi = sequence.getROIs();
        for (ROI roi : listroi) {
            if (roi.getName().contains("Point2D")) {
                sequence.removeROI(roi);
                removed = true;
            }
            if (roi.getName().contains("Point3D")) {
                sequence.removeROI(roi);
                removed = true;
            }
        }
        return removed;
    }
}
