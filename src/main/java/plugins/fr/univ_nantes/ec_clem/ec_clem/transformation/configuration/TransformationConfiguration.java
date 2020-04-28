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
package plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.configuration;

import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationType;

public class TransformationConfiguration {
    private TransformationType transformationType;
    private NoiseModel noiseModel;
    private boolean showGrid;

    public TransformationConfiguration(
        TransformationType transformationType,
        NoiseModel noiseModel,
        boolean showGrid
    ) {
        this.transformationType = transformationType;
        this.noiseModel = noiseModel;
        this.showGrid = showGrid;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public NoiseModel getNoiseModel() {
        return noiseModel;
    }

    public boolean isShowGrid() {
        return showGrid;
    }
}
