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
package plugins.fr.univ_nantes.ec_clem.sequence;

import icy.sequence.DimensionId;
import java.util.ArrayList;
import java.util.List;

public class SequenceSize {

    private List<DimensionSize> dimensions = new ArrayList<>();

    public List<DimensionSize> getDimensions() {
        return dimensions;
    }

    public int getN() {
        return dimensions.size();
    }

    public void add(DimensionSize dimensionSize) {
        dimensions.add(dimensionSize);
    }

    public DimensionSize get(DimensionId dimensionId) {
        for(DimensionSize dimension : dimensions) {
            if(dimension.getDimensionId().equals(dimensionId)) {
                return dimension;
            }
        }
        throw new RuntimeException("Dimension not found");
    }
}
