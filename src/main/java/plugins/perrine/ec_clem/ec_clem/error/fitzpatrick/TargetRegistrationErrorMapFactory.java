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
package plugins.perrine.ec_clem.ec_clem.error.fitzpatrick;

import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;
import javax.inject.Inject;

public class TargetRegistrationErrorMapFactory {

    private TREComputerFactory treComputerFactory;

    @Inject
    public TargetRegistrationErrorMapFactory(TREComputerFactory treComputerFactory) {
        this.treComputerFactory = treComputerFactory;
    }

    public TargetRegistrationErrorMapSupplier getFrom(Workspace workspace) {
        if(workspace.getTransformationSchema() == null) {
            throw new RuntimeException("Transformation is not initialized");
        }
        return new TargetRegistrationErrorMapSupplier(
            workspace.getTransformationSchema().getTargetSize(),
            treComputerFactory.getFrom(workspace)
        );
    }

    public TargetRegistrationErrorMapSupplier getFrom(TransformationSchema transformationSchema) {
        return new TargetRegistrationErrorMapSupplier(
            transformationSchema.getTargetSize(),
            treComputerFactory.getFrom(transformationSchema)
        );
    }
}
