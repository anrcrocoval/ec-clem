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
package plugins.perrine.ec_clem.ec_clem.workspace;

import plugins.perrine.ec_clem.ec_clem.monitor.MonitorTargetPoint;
import plugins.perrine.ec_clem.ec_clem.transformation.configuration.TransformationConfiguration;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import icy.sequence.Sequence;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Workspace {

    private Sequence sourceSequence;
    private Sequence targetSequence;
    private Sequence sourceBackup;
    private TransformationSchema transformationSchema;
    private File transformationSchemaOutputFile;
    private File csvTransformationOutputFile;
    private File xmlTransformationOutputFile;
    private WorkspaceState workspaceState;
    private TransformationConfiguration transformationConfiguration;
    private List<MonitorTargetPoint> monitorTargetPoints;

    public Workspace() {
        workspaceState = new WorkspaceState(false, false, null, false, false);
        monitorTargetPoints = new LinkedList<>();
    }

    public Workspace(
        Sequence sourceSequence,
        Sequence targetSequence,
        Sequence sourceBackup,
        File transformationSchemaOutputFile,
        File csvTransformationOutputFile,
        WorkspaceState workspaceState
    ) {
        this.sourceSequence = sourceSequence;
        this.targetSequence = targetSequence;
        this.sourceBackup = sourceBackup;
        this.transformationSchemaOutputFile = transformationSchemaOutputFile;
        this.csvTransformationOutputFile = csvTransformationOutputFile;
        this.workspaceState = workspaceState;
        monitorTargetPoints = new LinkedList<>();
    }

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

    public File getTransformationSchemaOutputFile() {
        return transformationSchemaOutputFile;
    }

    public void setTransformationSchemaOutputFile(File transformationSchemaOutputFile) {
        this.transformationSchemaOutputFile = transformationSchemaOutputFile;
    }

    public File getCsvTransformationOutputFile() {
        return csvTransformationOutputFile;
    }

    public void setCsvTransformationOutputFile(File csvTransformationOutputFile) {
        this.csvTransformationOutputFile = csvTransformationOutputFile;
    }

    public File getXmlTransformationOutputFile() {
        return xmlTransformationOutputFile;
    }

    public void setXmlTransformationOutputFile(File xmlTransformationOutputFile) {
        this.xmlTransformationOutputFile = xmlTransformationOutputFile;
    }

    public WorkspaceState getWorkspaceState() {
        return workspaceState;
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

    public List<MonitorTargetPoint> getMonitorTargetPoints() {
        return monitorTargetPoints;
    }

    public void addMonitoringPoint(MonitorTargetPoint monitorTargetPoint) {
        monitorTargetPoints.add(monitorTargetPoint);
    }
}
