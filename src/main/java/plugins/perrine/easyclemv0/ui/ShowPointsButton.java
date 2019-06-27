package plugins.perrine.easyclemv0.ui;

import com.google.common.io.Files;
import icy.gui.dialog.MessageDialog;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.dataset_transformer.TransformationDatasetTransformer;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.storage.xml.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static plugins.perrine.easyclemv0.storage.xml.XmlTransformation.transformationElementName;

public class ShowPointsButton extends JButton {

    private Workspace workspace;
    private DatasetFactory datasetFactory = new DatasetFactory();
    private XmlFileReader xmlFileReader = new XmlFileReader();
    private XmlTransformationReader xmlTransformationReader = new XmlTransformationReader();
    private RoiUpdater roiUpdater = new RoiUpdater();
    private TransformationDatasetTransformer transformationDatasetTransformer = new TransformationDatasetTransformer();

    public ShowPointsButton() {
        super("Show ROIs on original source image");
        setToolTipText("Show the original source Image, with the points selected shown (save the source image to save the ROIs)");
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

        boolean sorted = true;
        List<ROI> listRoisource = workspace.getSourceSequence().getROIs(sorted);
        if (listRoisource.size() > 0) {
            workspace.getSourceSequence().beginUpdate();
            workspace.getSourceSequence().removeAllImages();
            if (workspace.getSourceBackup() == null) {
                MessageDialog.showDialog("argh.");
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

            Dataset sourceDataset = datasetFactory.getFrom(workspace.getSourceSequence());
            Document document = xmlFileReader.loadFile(workspace.getXMLFile());
            ArrayList<Element> transformationElements = XMLUtil.getElements(document.getDocumentElement(), transformationElementName);
            for(Element element : transformationElements) {
                Transformation transformation = xmlTransformationReader.read(element);
                sourceDataset = transformationDatasetTransformer.apply(transformation, sourceDataset);
                roiUpdater.updateRoi(sourceDataset, workspace.getSourceSequence());
            }

            // Reinitialize XML FILE
            //AND CREATE A COPY of the former one for back up with the date
            String fileName = workspace.getXMLFile().getPath() +
                "_" + java.time.LocalDateTime.now().getDayOfMonth() +
                "_" + java.time.LocalDateTime.now().getMonth() +
                "_" + java.time.LocalDateTime.now().getHour() +
                "_" + java.time.LocalDateTime.now().getMinute() +
                "_backup.xml";
            File dest = new File(fileName);
            System.out.println("A back up of your transfo has been saved as" + fileName);

            try {
                Files.copy(workspace.getXMLFile(), dest);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            sequenceSizeXmlWriter = new SequenceSizeXmlWriter(xmlFileReader.loadFile(workspace.getXMLFile()));
//            sequenceSizeXmlWriter.writeSizeOf(workspace.getTargetSequence());

            saveRois(workspace.getSourceSequence());
            saveRois(workspace.getTargetSequence());
        }
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
