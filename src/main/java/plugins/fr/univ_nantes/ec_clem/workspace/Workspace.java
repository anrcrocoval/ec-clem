/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.fr.univ_nantes.ec_clem.workspace;

import plugins.fr.univ_nantes.ec_clem.transformation.configuration.TransformationConfiguration;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import icy.sequence.Sequence;
import plugins.fr.univ_nantes.ec_clem.monitor.MonitoringConfiguration;

import java.io.File;

public class Workspace {

    private Sequence sourceSequence;
    private Sequence targetSequence;
    private Sequence sourceBackup;
    private TransformationSchema transformationSchema;
    private File XMLFile;
    private WorkspaceState workspaceState;
    private TransformationConfiguration transformationConfiguration;

    public Workspace() {
        workspaceState = new WorkspaceState(false, false);
        monitoringConfiguration = new MonitoringConfiguration(false, false);
    }

    public Workspace(Sequence sourceSequence, Sequence targetSequence, Sequence sourceBackup, File XMLFile, WorkspaceState workspaceState, MonitoringConfiguration monitoringConfiguration) {
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

    public String getOriginalNameofSource () {
        return this.sourceBackup.getName();
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

    public TransformationSchema getTransformationSchema() {
        return transformationSchema;
    }

    public void setTransformationSchema(TransformationSchema transformationSchema) {
        this.transformationSchema = transformationSchema;
    }
}
