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
package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence;

import icy.sequence.DimensionId;

public class DimensionSize {
    private DimensionId dimensionId;
    private int size;
    private double pixelSizeInMicrometer;

    public DimensionSize(DimensionId dimensionId, int size, double pixelSizeInMicrometer) {
        this.dimensionId = dimensionId;
        this.size = size;
        this.pixelSizeInMicrometer = pixelSizeInMicrometer;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public int getSize() {
        return size;
    }

    public double getPixelSizeInMicrometer() {
        return pixelSizeInMicrometer;
    }
}
