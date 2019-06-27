package plugins.perrine.easyclemv0.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.dataset_transformer.TransformationDatasetTransformer;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.image_transformer.ImageTransformerFactory;
import plugins.perrine.easyclemv0.image_transformer.RigidImageTransformerInterface;
import plugins.perrine.easyclemv0.image_transformer.SequenceUpdater;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.storage.xml.*;
import javax.swing.*;
import java.util.List;

public class UndoButton extends JButton {

    private Workspace workspace;
    private XmlTransformationReader xmlTransformationReader = new XmlTransformationReader();
    private XmlTransformationWriter xmlTransformationWriter;
    private XmlFileReader xmlFileReader = new XmlFileReader();
    private XmlFileWriter xmlFileWriter;
    private DatasetFactory datasetFactory = new DatasetFactory();
    private DatasetTransformer datasetTransformer = new DatasetTransformer();
    private RoiUpdater roiUpdater = new RoiUpdater();
    private ImageTransformerFactory imageTransformerFactory = new ImageTransformerFactory();
    private TransformationDatasetTransformer transformationDatasetTransformer = new TransformationDatasetTransformer();
    private XmlTransformationElementListReader xmlTransformationElementListReader = new XmlTransformationElementListReader();
    private XmlTransformationElementListWriter xmlTransformationElementListWriter = new XmlTransformationElementListWriter();
    private SequenceUpdater sequenceUpdater = new SequenceUpdater();

    public UndoButton() {
        super("Undo last point");
        setToolTipText("Press this button to cancel the last point edition you have done, it will reverse to the previous state of your image");
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

        List<ROI> listRoisource = workspace.getSourceSequence().getROIs(true);
        if (listRoisource.size() == 0) {
            new AnnounceFrame("Nothing to undo",5);
            return;
        }

        List<ROI> listRoitarget = workspace.getTargetSequence().getROIs(true);
        ROI roitoremove = listRoisource.get(listRoisource.size() - 1);
        roitoremove.remove();
        if (listRoitarget.size() >= listRoisource.size()) {
            ROI roitoremovet = listRoitarget.get(listRoisource.size() - 1);
            roitoremovet.remove();
        }

        workspace.getSourceSequence().beginUpdate();
        workspace.getSourceSequence().removeAllImages();

        if (workspace.getSourceBackup() == null) {
            MessageDialog.showDialog("Argh.");
            return;
        }

        try {
            for (int t = 0; t < workspace.getSourceBackup().getSizeT(); t++) {
                for (int z = 0; z < workspace.getSourceBackup().getSizeZ(); z++) {
                    workspace.getSourceSequence().setImage(t, z,
                        workspace.getSourceBackup().getImage(t, z)
                    );
                }
            }
        } finally {
            workspace.getSourceSequence().endUpdate();
        }

//        Dataset sourceDataset = datasetFactory.getFrom(workspace.getSourceSequence());
//        Document document = xmlFileReader.loadFile(workspace.getXMLFile());
//        ArrayList<Element> transformationElements = XMLUtil.getElements(document.getDocumentElement(), transformationElementName);
//        transformationElements.remove(transformationElements.size() - 1);
//        for(Element element : transformationElements) {
//            Transformation transformation = xmlTransformationReader.read(element, sequenceSizeXmlReader.readSize(document));
//            sourceDataset = transformationDatasetTransformer.apply(transformation, sourceDataset);
//            roiUpdater.updateRoi(sourceDataset, workspace.getSourceSequence());
//        }

        Document document = xmlFileReader.loadFile(workspace.getXMLFile());
        xmlTransformationElementListWriter.removeLastTransformationElement(document);
        xmlFileWriter = new XmlFileWriter(document, workspace.getXMLFile());
        xmlFileWriter.write();

//            Element newsizeelement = XMLUtil.getElements( root , "TargetSize" ).get(0);
//            int width = XMLUtil.getAttributeIntValue( newsizeelement, "width" , -1 );
//            int height = XMLUtil.getAttributeIntValue( newsizeelement, "height" , -1 );
//
//            int nbz = XMLUtil.getAttributeIntValue( newsizeelement, "nz" , -1 );

        List<Transformation> transformationList = xmlTransformationElementListReader.getTransformationList(document);
        for(Transformation transformation : transformationList) {
            sequenceUpdater.update(workspace.getSourceSequence(), transformation);
        }
    }
}
