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

import plugins.fr.univ_nantes.ec_clem.error.fitzpatrick.TREComputer;
import plugins.fr.univ_nantes.ec_clem.error.fitzpatrick.TREComputerFactory;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.DatasetFactory;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.roi.RoiUpdater;
import plugins.fr.univ_nantes.ec_clem.storage.transformation.TransformationToCsvFileWriter;
import plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.writer.TransformationSchemaToXmlFileWriter;
import plugins.fr.univ_nantes.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchemaFactory;
import icy.gui.viewer.Viewer;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import plugins.fr.univ_nantes.ec_clem.monitor.MonitorTargetPoint;
import plugins.fr.univ_nantes.ec_clem.progress.ProgressTrackableMasterTask;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceFactory;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceUpdater;
import javax.inject.Inject;

public class WorkspaceTransformer extends ProgressTrackableMasterTask implements Runnable {

    private TransformationSchemaFactory transformationSchemaFactory;
    private TREComputerFactory treComputerFactory;
    private SequenceFactory sequenceFactory;
    private RegistrationParameterFactory registrationParameterFactory;
    private RoiUpdater roiUpdater;
    private DatasetFactory datasetFactory;

    private TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter;
    private TransformationToCsvFileWriter transformationToCsvFileWriter;

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
        roiUpdater.updateRoi(datasetFactory.getFrom(workspace.getTargetSequence(), PointType.FIDUCIAL), workspace.getTargetSequence());
        transformationSchemaToXmlFileWriter.save(workspace.getTransformationSchema(), workspace.getTransformationSchemaOutputFile());
        transformationToCsvFileWriter.save(registrationParameterFactory.getFrom(workspace.getTransformationSchema()).getTransformation(), workspace.getTransformationOutputFile());

        for(MonitorTargetPoint monitorTargetPoint : workspace.getMonitorTargetPoints()) {
            if(monitorTargetPoint.getMonitoringPoint() != null) {
                TREComputer treComputer = treComputerFactory.getFrom(workspace);
                monitorTargetPoint.UpdatePoint(
                        workspace.getTransformationSchema().getFiducialSet().getN(),
                        treComputer.getExpectedSquareTRE(monitorTargetPoint.getMonitoringPoint())
                );
            }
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

    @Inject
    public void setTransformationSchemaToXmlFileWriter(TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter) {
        this.transformationSchemaToXmlFileWriter = transformationSchemaToXmlFileWriter;
    }

    @Inject
    public void setTransformationToCsvFileWriter(TransformationToCsvFileWriter transformationToCsvFileWriter) {
        this.transformationToCsvFileWriter = transformationToCsvFileWriter;
    }

    @Inject
    public void setRegistrationParameterFactory(RegistrationParameterFactory registrationParameterFactory) {
        this.registrationParameterFactory = registrationParameterFactory;
    }

    @Inject
    public void setRoiUpdater(RoiUpdater roiUpdater) {
        this.roiUpdater = roiUpdater;
    }

    @Inject
    public void setDatasetFactory(DatasetFactory datasetFactory) {
        this.datasetFactory = datasetFactory;
    }
}
