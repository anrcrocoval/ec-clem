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
package plugins.perrine.ec_clem.ec_clem.fiducialset;

import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;
import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.DatasetFactory;

import javax.inject.Inject;

public class FiducialSetFactory {

    private DatasetFactory datasetFactory;

    @Inject
    public FiducialSetFactory(DatasetFactory datasetFactory) {
        this.datasetFactory = datasetFactory;
    }

    public FiducialSet getFrom(Workspace workspace) {
        return new FiducialSet(
            datasetFactory.getFrom(workspace.getSourceSequence(), PointType.FIDUCIAL),
            datasetFactory.getFrom(workspace.getTargetSequence(), PointType.FIDUCIAL)
        );
    }
}
