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
package plugins.perrine.easyclemv0.ec_clem.transformation.schema;

import icy.sequence.DimensionId;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.ec_clem.sequence.SequenceName;
import plugins.perrine.easyclemv0.ec_clem.sequence.SequenceSize;
import plugins.perrine.easyclemv0.ec_clem.sequence.SequenceSize;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.FiducialSet;

public class TransformationSchema {
    private FiducialSet fiducialSet;
    private TransformationType transformationType;
    private NoiseModel noiseModel;
    private SequenceSize sourceSize;
    private SequenceSize targetSize;
    private SequenceName sourceName;
    private SequenceName targetName;

    public TransformationSchema(
        FiducialSet fiducialSet,
        TransformationType transformationType,
        NoiseModel noiseModel,
        SequenceSize sourceSize,
        SequenceSize targetSize
    ) {
        this.fiducialSet = fiducialSet;
        this.transformationType = transformationType;
        this.noiseModel = noiseModel;
        this.sourceSize = sourceSize;
        this.targetSize = targetSize;
        this.sourceName=new SequenceName();
        this.targetName=new SequenceName();
    }

    public TransformationSchema(FiducialSet fiducialSet, TransformationType transformationType,
			NoiseModel noiseModel, SequenceSize sourceSize, SequenceSize targetSize, SequenceName sourceName, SequenceName targetName) {
    	 this.fiducialSet = fiducialSet;
         this.transformationType = transformationType;
         this.noiseModel = noiseModel;
         this.sourceSize = sourceSize;
         this.targetSize = targetSize;
         this.sourceName=sourceName;
         this.targetName=targetName;
         
	}

	public FiducialSet getFiducialSet() {
        return fiducialSet;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public NoiseModel getNoiseModel() {
        return noiseModel;
    }

    public SequenceSize getSourceSize() {
        return sourceSize;
    }

    public SequenceSize getTargetSize() {
        return targetSize;
    }
    public void setTargetSize(SequenceSize targetsize) {
        targetSize=targetsize;
    }
    public SequenceName getSourceName() {
    	return sourceName;
    }
    
    public SequenceName getTargetName() {
    	return targetName;
    }
    public TransformationSchema inverse() {
        return new TransformationSchema(
            new FiducialSet(fiducialSet.getTargetDataset(), fiducialSet.getSourceDataset()),
            transformationType,
            noiseModel,
            targetSize,
            sourceSize,
            targetName,
            sourceName
        );
    }
}
