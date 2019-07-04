package plugins.perrine.easyclemv0.model;

import icy.gui.frame.progress.AnnounceFrame;
import icy.sequence.Sequence;
import icy.sequence.SequenceListener;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.factory.FiducialSetFactory;
import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.factory.TransformationSchemaFactory;
import plugins.perrine.easyclemv0.image_transformer.SequenceUpdater;
import plugins.perrine.easyclemv0.monitor.MonitorTargetPoint;
import plugins.perrine.easyclemv0.sequence_listener.RoiAdded;
import plugins.perrine.easyclemv0.storage.xml.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.xml.XmlTransformationWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WorkspaceTransformer {

    private SequenceUpdater sequenceUpdater = new SequenceUpdater();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private TransformationSchemaFactory transformationSchemaFactory = new TransformationSchemaFactory();
    private TREComputerFactory treComputerFactory = new TREComputerFactory();
    private FiducialSetFactory fiducialSetFactory = new FiducialSetFactory();

    private List<Integer> listofNvalues = new ArrayList<>();
    private List<Double> listoftrevalues = new ArrayList<>();

    private XmlFileWriter xmlFileWriter;
    private XmlTransformationWriter xmlWriter;

    public void apply(Workspace workspace) {
        executorService.submit(() -> {
//            SequenceListener[] sourceSequenceListeners = removeListeners(workspace.getSourceSequence());
            List<SequenceListener> targetSequenceListeners = removeListeners(workspace.getTargetSequence());
            resetToOriginalImage(workspace);

            try {
                workspace.setTransformationSchema(transformationSchemaFactory.getFrom(workspace));
            } catch (RuntimeException e) {
                addListeners(workspace.getTargetSequence(), targetSequenceListeners);
            }

            sequenceUpdater.update(workspace.getSourceSequence(), workspace.getTransformationSchema());
            Document document = XMLUtil.createDocument(true);
            xmlWriter = new XmlTransformationWriter(document);
            xmlWriter.write(workspace.getTransformationSchema());
            xmlFileWriter = new XmlFileWriter(document, workspace.getXMLFile());
            xmlFileWriter.write();

            if (workspace.getMonitoringConfiguration().isMonitor()) {
                FiducialSet fiducialSet = fiducialSetFactory.getFrom(workspace);
                TREComputer treComputer = treComputerFactory.getFrom(fiducialSet);

                listofNvalues.add(listofNvalues.size(), fiducialSet.getN());
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

//            addListeners(workspace.getSourceSequence(), sourceSequenceListeners);
            addListeners(workspace.getTargetSequence(), targetSequenceListeners);
        });
    }

    public List<SequenceListener> removeListeners(Sequence sequence) {
        List<SequenceListener> listeners = Arrays.stream(sequence.getListeners()).filter((listener) -> listener instanceof RoiAdded).collect(Collectors.toList());
        for(SequenceListener listener : listeners) {
            sequence.removeListener(listener);
        }
        return listeners;
    }

    public void addListeners(Sequence sequence, List<SequenceListener> listeners) {
        for(SequenceListener listener : listeners) {
            sequence.addListener(listener);
        }
    }

    public void resetToOriginalImage(Workspace workspace) {
        if(workspace.getTransformationSchema() != null) {
            sequenceUpdater.update(workspace.getSourceSequence(), workspace.getTransformationSchema().inverse());
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
        }
        finally {
            sequence.endUpdate();
        }
    }
}
