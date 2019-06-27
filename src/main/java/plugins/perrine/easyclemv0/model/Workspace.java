package plugins.perrine.easyclemv0.model;

import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.model.configuration.TransformationConfiguration;
import plugins.perrine.easyclemv0.monitor.MonitoringConfiguration;

import java.io.File;

public class Workspace {

    private Sequence sourceSequence;
    private Sequence targetSequence;
    private Sequence sourceBackup;
    private Transformation transformation;
    private File XMLFile;
    private WorkspaceState workspaceState;
    private TransformationConfiguration transformationConfiguration;

    public Workspace() {
        workspaceState = new WorkspaceState(false, false, false, false);
        monitoringConfiguration = new MonitoringConfiguration(false, false);
    }

    public Workspace(Sequence sourceSequence, Sequence targetSequence, Sequence sourceBackup, File XMLFile, boolean nonRigid, WorkspaceState workspaceState, MonitoringConfiguration monitoringConfiguration) {
        this.sourceSequence = sourceSequence;
        this.targetSequence = targetSequence;
        this.sourceBackup = sourceBackup;
        this.XMLFile = XMLFile;
        this.workspaceState = workspaceState;
        this.monitoringConfiguration = monitoringConfiguration;
    }

    private MonitoringConfiguration monitoringConfiguration;

    public Sequence getSourceSequence() {
        return sourceSequence;
    }

    public void setSourceSequence(Sequence sourceSequence) {
        this.sourceSequence = sourceSequence;
    }

    public Sequence getSourceBackup() {
        return sourceBackup;
    }

    public void setSourceBackup(Sequence sourceBackup) {
        this.sourceBackup = sourceBackup;
    }

    public Sequence getTargetSequence() {
        return targetSequence;
    }

    public void setTargetSequence(Sequence targetSequence) {
        this.targetSequence = targetSequence;
    }

    public File getXMLFile() {
        return XMLFile;
    }

    public void setXMLFile(File XMLFile) {
        this.XMLFile = XMLFile;
    }

    public WorkspaceState getWorkspaceState() {
        return workspaceState;
    }

    public MonitoringConfiguration getMonitoringConfiguration() {
        return monitoringConfiguration;
    }

    public TransformationConfiguration getTransformationConfiguration() {
        return transformationConfiguration;
    }

    public void setTransformationConfiguration(TransformationConfiguration transformationConfiguration) {
        this.transformationConfiguration = transformationConfiguration;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }
}
