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
package test.plugins.perrine.easyclemv0.transformation.schema;

import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.schema.TransformationType;

import javax.inject.Inject;

public class TestTransformationSchemaFactory {

    @Inject
    public TestTransformationSchemaFactory() {
    }

    public TransformationSchema getRigidTransformationSchema(FiducialSet fiducialSet, TransformationType transformationType, SequenceSize sourceSize, SequenceSize targetSize) {
        return new TransformationSchema(
            fiducialSet,
            transformationType,
            sourceSize,
            targetSize
        );
    }
}