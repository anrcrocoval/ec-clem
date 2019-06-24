package plugins.perrine.easyclemv0.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.main.Icy;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.sequence.SequenceListener;
import org.w3c.dom.Document;
import plugins.kernel.roi.roi2d.plugin.ROI2DPointPlugin;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.factory.FiducialSetFactory;
import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.factory.TransformationFactory;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Transformation;
import plugins.perrine.easyclemv0.model.Workspace;
import plugins.perrine.easyclemv0.model.WorkspaceTransformer;
import plugins.perrine.easyclemv0.monitor.MonitorTargetPoint;
import plugins.perrine.easyclemv0.storage.xml.SequenceSizeXmlWriter;
import plugins.perrine.easyclemv0.storage.xml.XmlFileReader;
import plugins.perrine.easyclemv0.storage.xml.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.xml.XmlTransformationWriter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateTransformationButton extends JButton {

    private WorkspaceTransformer workspaceTransformer = new WorkspaceTransformer();
    private Workspace workspace;
    private TransformationFactory transformationFactory = new TransformationFactory();
    private TREComputerFactory treComputerFactory = new TREComputerFactory();
    private FiducialSetFactory fiducialSetFactory = new FiducialSetFactory();

    private List<Integer> listofNvalues = new ArrayList<>();
    private List<Double> listoftrevalues = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private XmlFileReader xmlFileReader = new XmlFileReader();
    private XmlFileWriter xmlFileWriter;
    private XmlTransformationWriter xmlWriter;
    private SequenceSizeXmlWriter sequenceSizeXmlWriter;



    public UpdateTransformationButton() {
        super("Update Transformation");
        setToolTipText("Press this button if you have moved the points, prepared set of points, \n or obtained some black part of the image. This will refresh it");
        addActionListener((arg0) -> action());
    }


    private void action() {
        if (workspace.getWorkspaceState().isStopFlag()) {
            return;
        }

        if (workspace.getSourceBackup() == null) {
            MessageDialog.showDialog("Please press the Play button to initialize process first");
            return;
        }

        executorService.submit(() -> {
//            SequenceListener[] sourceSequenceListeners = removeListeners(workspace.getSourceSequence());
            SequenceListener[] targetSequenceListeners = removeListeners(workspace.getTargetSequence());

            Transformation transformation = transformationFactory.getFrom(workspace);

            if (!workspace.getWorkspaceState().isPause()) {
                workspaceTransformer.apply(workspace.getSourceSequence(), transformation);
                xmlWriter.write(transformation);
                xmlFileWriter.write();

                if (workspace.getMonitoringConfiguration().isMonitor()) {
                    FiducialSet fiducialSet = fiducialSetFactory.getFrom(workspace);
//            TargetRegistrationErrorMap ComputeFRE = new TargetRegistrationErrorMap();
//            ComputeFRE.setSequence(targetSequence);
                    TREComputer treComputer = treComputerFactory.getFrom(fiducialSet);
//            double FLEmax = fleComputer.maxdifferrorinnm(sourceDataset, targetDataset, sourceSequence.getPixelSizeX(), targetSequence.getPixelSizeX());

//            System.out.println("Max localization error FLE estimated " + FLEmax + " nm");
//            if (monitorTargetOnSource) { // in that case we need to update the position of target
//                monitoringPoint = similarity.apply(monitoringPoint);
//            }
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

                workspace.getSourceSequence().getFirstViewer().getLutViewer().setAutoBound(false);
                new AnnounceFrame("Transformation Updated", 5);
            } else {
                new AnnounceFrame("You are in pause mode, click on update transfo", 3);
                Icy.getMainInterface().setSelectedTool(ROI2DPointPlugin.class.getName());
            }

//            addListeners(workspace.getSourceSequence(), sourceSequenceListeners);
            addListeners(workspace.getTargetSequence(), targetSequenceListeners);
        });
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        Document document = xmlFileReader.loadFile(workspace.getXMLFile());
        xmlFileWriter = new XmlFileWriter(document, workspace.getXMLFile());
        xmlWriter = new XmlTransformationWriter(document);
        sequenceSizeXmlWriter = new SequenceSizeXmlWriter(document);
        sequenceSizeXmlWriter.writeSizeOf(workspace.getTargetSequence());
        xmlFileWriter.write();
    }

    private SequenceListener[] removeListeners(Sequence sequence) {
        SequenceListener[] listeners = sequence.getListeners();
        for(SequenceListener listener : listeners) {
            sequence.removeListener(listener);
        }
        return listeners;
    }

    private void addListeners(Sequence sequence, SequenceListener[] listeners) {
        for(SequenceListener listener : listeners) {
            sequence.addListener(listener);
        }
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
