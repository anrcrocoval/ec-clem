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
import java.util.ArrayList;
import java.util.List;

public class WorkspaceTransformer extends ProgressTrackableMasterTask implements Runnable {

    private TransformationSchemaFactory transformationSchemaFactory;
    private TREComputerFactory treComputerFactory;
    private SequenceFactory sequenceFactory;
    private RegistrationParameterFactory registrationParameterFactory;

    private List<Integer> listofNvalues = new ArrayList<>();
    private List<Double> listoftrevalues = new ArrayList<>();

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
        transformationSchemaToXmlFileWriter.save(workspace.getTransformationSchema(), workspace.getTransformationSchemaOutputFile());
        transformationToCsvFileWriter.save(registrationParameterFactory.getFrom(workspace.getTransformationSchema()).getTransformation(), workspace.getTransformationOutputFile());

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
}
