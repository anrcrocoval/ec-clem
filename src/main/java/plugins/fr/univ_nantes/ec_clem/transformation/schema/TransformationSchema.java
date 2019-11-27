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
package plugins.fr.univ_nantes.ec_clem.transformation.schema;

import plugins.fr.univ_nantes.ec_clem.sequence.SequenceSize;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;

public class TransformationSchema {
    private FiducialSet fiducialSet;
    private TransformationType transformationType;
    private SequenceSize sourceSize;
    private SequenceSize targetSize;

    public TransformationSchema(FiducialSet fiducialSet, TransformationType transformationType, SequenceSize sourceSize, SequenceSize targetSize) {
        this.fiducialSet = fiducialSet;
        this.transformationType = transformationType;
        this.sourceSize = sourceSize;
        this.targetSize = targetSize;
    }

    public FiducialSet getFiducialSet() {
        return fiducialSet;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public SequenceSize getSourceSize() {
        return sourceSize;
    }

    public SequenceSize getTargetSize() {
        return targetSize;
    }

    public TransformationSchema inverse() {
        return new TransformationSchema(
            new FiducialSet(fiducialSet.getTargetDataset(), fiducialSet.getSourceDataset()),
            transformationType,
            targetSize,
            sourceSize
        );
    }
}
