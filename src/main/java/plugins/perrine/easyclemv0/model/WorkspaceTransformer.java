package plugins.perrine.easyclemv0.model;

import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.viewer.Viewer;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.factory.*;
import plugins.perrine.easyclemv0.image_transformer.SequenceUpdater;
import plugins.perrine.easyclemv0.monitor.MonitorTargetPoint;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.storage.xml.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.xml.XmlTransformationWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkspaceTransformer {

    private SequenceUpdater sequenceUpdater = new SequenceUpdater();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private TransformationSchemaFactory transformationSchemaFactory = new TransformationSchemaFactory();
    private TREComputerFactory treComputerFactory = new TREComputerFactory();
    private RoiUpdater roiUpdater = new RoiUpdater();
    private SequenceFactory sequenceFactory = new SequenceFactory();
    private DatasetFactory datasetFactory = new DatasetFactory();

    private List<Integer> listofNvalues = new ArrayList<>();
    private List<Double> listoftrevalues = new ArrayList<>();

    private XmlFileWriter xmlFileWriter;
    private XmlTransformationWriter xmlWriter;

    public void apply(Workspace workspace) {
        executorService.submit(() -> {
            resetToOriginalImage(workspace);
            workspace.setTransformationSchema(transformationSchemaFactory.getFrom(workspace));
            if(workspace.getTransformationConfiguration().isShowGrid()) {
                Sequence gridSequence = sequenceFactory.getGridSequence(
                        workspace.getSourceSequence().getSizeX(),
                        workspace.getSourceSequence().getSizeY(),
                        workspace.getSourceSequence().getSizeZ(),
                        workspace.getSourceSequence().getPixelSizeX(),
                        workspace.getSourceSequence().getPixelSizeY(),
                        workspace.getSourceSequence().getPixelSizeZ()
                );
                sequenceUpdater.update(gridSequence, workspace.getTransformationSchema());
                ThreadUtil.invokeLater(() -> new Viewer(gridSequence));
            }
            sequenceUpdater.update(workspace.getSourceSequence(), workspace.getTransformationSchema());
            Document document = XMLUtil.createDocument(true);
            xmlWriter = new XmlTransformationWriter(document);
            xmlWriter.write(workspace.getTransformationSchema());
            xmlFileWriter = new XmlFileWriter(document, workspace.getXMLFile());
            xmlFileWriter.write();

            if (workspace.getMonitoringConfiguration().isMonitor()) {
                TREComputer treComputer = treComputerFactory.getFrom(workspace);

                listofNvalues.add(listofNvalues.size(), workspace.getTransformationSchema().getFiducialSet().getN());
                listoftrevalues.add(
                    listoftrevalues.size(),
                    treComputer.getExpectedSquareTRE(workspace.getMonitoringConfiguration().getMonitoringPoint())
                );

                double[][] TREValues = new double[listofNvalues.size()][2];

                for (int i = 0; i < listofNvalues.size(); i++) {
                    TREValues[i][0] = listofNvalues.get(i);
                    TREValues[i][1] = listoftrevalues.get(i);
                    System.out.println("N=" + TREValues[i][0] + ", TRE=" + TREValues[i][1]);
                }
                MonitorTargetPoint.UpdatePoint(TREValues);
            }
            new AnnounceFrame("TransformationSchema Updated", 5);
        });
    }

    public void resetToOriginalImage(Workspace workspace) {
        if(workspace.getTransformationSchema() != null) {
            Dataset reversed = datasetFactory.getFrom(
                    datasetFactory.getFrom(workspace.getSourceSequence()),
                    workspace.getTransformationSchema().inverse()
            );
            restoreBackup(workspace.getSourceSequence(), workspace.getSourceBackup());
            roiUpdater.updateRoi(reversed, workspace.getSourceSequence());
            workspace.setTransformationSchema(null);
        }
    }

    private void restoreBackup(Sequence sequence, Sequence backup) {
        sequence.setAutoUpdateChannelBounds(false);
        sequence.beginUpdate();
        sequence.removeAllImages();
        try {
            for (int t = 0; t < backup.getSizeT(); t++) {
                for (int z = 0; z < backup.getSizeZ(); z++) {
                    sequence.setImage(t, z, backup.getImage(t, z));
                }
            }
            sequence.setPixelSizeX(backup.getPixelSizeX());
            sequence.setPixelSizeY(backup.getPixelSizeY());
            sequence.setPixelSizeZ(backup.getPixelSizeZ());
        } finally {
            sequence.endUpdate();
        }
    }
}
