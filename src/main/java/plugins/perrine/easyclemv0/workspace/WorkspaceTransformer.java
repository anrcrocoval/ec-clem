package plugins.perrine.easyclemv0.workspace;

import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.viewer.Viewer;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputerFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputer;
import plugins.perrine.easyclemv0.sequence.SequenceUpdater;
import plugins.perrine.easyclemv0.sequence.SequenceFactory;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchemaFactory;
import plugins.perrine.easyclemv0.monitor.MonitorTargetPoint;
import plugins.perrine.easyclemv0.roi.RoiUpdater;
import plugins.perrine.easyclemv0.storage.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.XmlTransformationWriter;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkspaceTransformer {

    private SequenceUpdater sequenceUpdater;
    private ExecutorService executorService;
    private TransformationSchemaFactory transformationSchemaFactory;
    private TREComputerFactory treComputerFactory;
    private RoiUpdater roiUpdater;
    private SequenceFactory sequenceFactory;
    private DatasetFactory datasetFactory;

    @Inject
    public WorkspaceTransformer(SequenceUpdater sequenceUpdater , TransformationSchemaFactory transformationSchemaFactory, TREComputerFactory treComputerFactory, RoiUpdater roiUpdater, SequenceFactory sequenceFactory, DatasetFactory datasetFactory) {
        this.sequenceUpdater = sequenceUpdater;
        this.transformationSchemaFactory = transformationSchemaFactory;
        this.treComputerFactory = treComputerFactory;
        this.roiUpdater = roiUpdater;
        this.sequenceFactory = sequenceFactory;
        this.datasetFactory = datasetFactory;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

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
            new AnnounceFrame("Transformation Updated", 5);
            workspace.getSourceSequence().setName(workspace.getOriginalNameofSource()+ "(transformed)");
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
            workspace.getSourceSequence().setName(workspace.getOriginalNameofSource());
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
