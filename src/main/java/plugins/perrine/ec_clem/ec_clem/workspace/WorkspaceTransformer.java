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

import plugins.perrine.correlativeview.TestVisu;
import plugins.perrine.ec_clem.ec_clem.error.fitzpatrick.TREComputer;
import plugins.perrine.ec_clem.ec_clem.error.fitzpatrick.TREComputerFactory;
import plugins.perrine.ec_clem.ec_clem.monitor.MonitorTargetPoint;
import plugins.perrine.ec_clem.ec_clem.progress.ProgressTrackableMasterTask;
import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.roi.RoiUpdater;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceFactory;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceUpdater;
import plugins.perrine.ec_clem.ec_clem.storage.transformation.csv.TransformationToCsvFileWriter;
import plugins.perrine.ec_clem.ec_clem.storage.transformation.xml.TransformationToXmlFileWriter;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.writer.TransformationSchemaToXmlFileWriter;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.DatasetFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.perrine.ec_clem.ec_clem.transformation.RegistrationParameterFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchemaFactory;
import icy.canvas.Canvas2D;
import icy.canvas.IcyCanvas;
import icy.gui.viewer.Viewer;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
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
    private TransformationToXmlFileWriter transformationToXmlFileWriter;

    private Workspace workspace;

    public WorkspaceTransformer(Workspace workspace) {
        DaggerWorkspaceTransformerComponent.builder().build().inject(this);
        this.workspace = workspace;
    }

    @SuppressWarnings("deprecation")
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
        roiUpdater.updateMeasuredErrorRoi(
            datasetFactory.toPixel(datasetFactory.getFrom(workspace.getSourceSequence(), PointType.FIDUCIAL), workspace.getSourceSequence()),
            datasetFactory.toPixel(datasetFactory.getFrom(workspace.getTargetSequence(), PointType.FIDUCIAL), workspace.getTargetSequence()),
            workspace.getSourceSequence()
        );
        
        roiUpdater.setLayersVisible(workspace.getSourceSequence(), PointType.MEASURED_ERROR, workspace.getWorkspaceState().isShowMeasuredError());
        roiUpdater.setLayersVisible(workspace.getSourceSequence(), PointType.PREDICTED_ERROR, workspace.getWorkspaceState().isShowPredictedError());
        transformationSchemaToXmlFileWriter.save(workspace.getTransformationSchema(), workspace.getTransformationSchemaOutputFile());
        transformationToCsvFileWriter.save(registrationParameterFactory.getFrom(workspace.getTransformationSchema()).getTransformation(), workspace.getCsvTransformationOutputFile());
        transformationToXmlFileWriter.save(
            registrationParameterFactory.getFrom(workspace.getTransformationSchema()).getTransformation(),
            workspace.getTransformationSchema(),
            workspace.getXmlTransformationOutputFile()
        );
        
        for(MonitorTargetPoint monitorTargetPoint : workspace.getMonitorTargetPoints()) {
            if(monitorTargetPoint.getMonitoringPoint() != null) {
                TREComputer treComputer = treComputerFactory.getFrom(workspace);
                monitorTargetPoint.UpdatePoint(
                        workspace.getTransformationSchema().getFiducialSet().getN(),
                        treComputer.getExpectedSquareTRE(monitorTargetPoint.getMonitoringPoint())
                );
            }
        }
        /*workspace.getTargetSequence().getOverlays();
       // workspace.getTargetSequence().getFirstViewer().setCanvas(workspace.getSourceSequence().getFirstViewer().getCanvas().getName());
        if(registrationParameterFactory.getFrom(workspace.getTransformationSchema()).getTransformation() instanceof AffineTransformation) {
            
        	AffineTransformation transfomatrix=(AffineTransformation) registrationParameterFactory.getFrom(workspace.getTransformationSchema()).getTransformation();
        	
        	workspace.getTargetSequence().getFirstViewer().setCanvas(new TestVisu(workspace.getTargetSequence().getFirstViewer(),workspace.getSourceBackup().getFirstImage(), 
        		transfomatrix.getHomogeneousMatrix4x4().getArray(), 
        		workspace.getTransformationSchema().getTargetSize().getDimensions().get(0).getPixelSizeInMicrometer(),
        		workspace.getTransformationSchema().getSourceSize().getDimensions().get(0).getPixelSizeInMicrometer()));
        }*/
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

    @Inject
    public void setTransformationToXmlFileWriter(TransformationToXmlFileWriter transformationToXmlFileWriter) {
        this.transformationToXmlFileWriter = transformationToXmlFileWriter;
    }
}
