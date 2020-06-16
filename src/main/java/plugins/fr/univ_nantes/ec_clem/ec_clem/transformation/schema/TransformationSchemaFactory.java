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
package plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema;

import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.FiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence.SequenceSizeFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.Workspace;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence.SequenceSizeFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.FiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.Workspace;

import javax.inject.Inject;

public class TransformationSchemaFactory {

    private FiducialSetFactory fiducialSetFactory;
    private SequenceSizeFactory sequenceSizeFactory;

    @Inject
    public TransformationSchemaFactory(FiducialSetFactory fiducialSetFactory, SequenceSizeFactory sequenceSizeFactory) {
        this.fiducialSetFactory = fiducialSetFactory;
        this.sequenceSizeFactory = sequenceSizeFactory;
    }

    public TransformationSchema getFrom(Workspace workspace) {
        FiducialSet fiducialSet = fiducialSetFactory.getFrom(workspace);
        return new TransformationSchema(
            fiducialSet,
            workspace.getTransformationConfiguration().getTransformationType(),
            workspace.getTransformationConfiguration().getNoiseModel(),
            sequenceSizeFactory.getFrom(workspace.getSourceSequence()),
            sequenceSizeFactory.getFrom(workspace.getTargetSequence())
        );
    }
}
