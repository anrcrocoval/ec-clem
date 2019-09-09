package plugins.perrine.easyclemv0.workspace;

import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.viewer.Viewer;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputer;
import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputerFactory;
import plugins.perrine.easyclemv0.monitor.MonitorTargetPoint;
import plugins.perrine.easyclemv0.progress.ProgressTrackableMasterTask;
import plugins.perrine.easyclemv0.sequence.SequenceFactory;
import plugins.perrine.easyclemv0.sequence.SequenceUpdater;
import plugins.perrine.easyclemv0.storage.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.XmlTransformationWriter;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchemaFactory;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceTransformer extends ProgressTrackableMasterTask implements Runnable {

    private TransformationSchemaFactory transformationSchemaFactory;
    private TREComputerFactory treComputerFactory;
    private SequenceFactory sequenceFactory;

    private List<Integer> listofNvalues = new ArrayList<>();
    private List<Double> listoftrevalues = new ArrayList<>();
    private XmlFileWriter xmlFileWriter;
    private XmlTransformationWriter xmlWriter;

    private Workspace workspace;

    public WorkspaceTransformer(Workspace workspace) {
        DaggerWorkspaceTransformerComponent.builder().build().inject(this);
        this.workspace = workspace;
    }

    @Override
    public void run() {
        ResetOriginalImage resetOriginalImage = new ResetOriginalImage(workspace);
        super.add(resetOriginalImage);
        resetOriginalImage.run();
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
            SequenceUpdater transformationGridSequenceUpdater = new SequenceUpdater(gridSequence, workspace.getTransformationSchema());
            super.add(transformationGridSequenceUpdater);
            transformationGridSequenceUpdater.run();
            ThreadUtil.invokeLater(() -> new Viewer(gridSequence));
        }
        SequenceUpdater sequenceUpdater = new SequenceUpdater(workspace.getSourceSequence(), workspace.getTransformationSchema());
        super.add(sequenceUpdater);
        sequenceUpdater.run();

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
    }

    @Inject
    public void setTransformationSchemaFactory(TransformationSchemaFactory transformationSchemaFactory) {
        this.transformationSchemaFactory = transformationSchemaFactory;
    }

    @Inject
    public void setTreComputerFactory(TREComputerFactory treComputerFactory) {
        this.treComputerFactory = treComputerFactory;
    }

    @Inject
    public void setSequenceFactory(SequenceFactory sequenceFactory) {
        this.sequenceFactory = sequenceFactory;
    }
}
