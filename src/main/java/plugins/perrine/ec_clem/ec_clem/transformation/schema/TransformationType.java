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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TransformationType {
    AFFINE,
    RIGID,
    SIMILARITY,
    SPLINE;

    public static String[] toArray() {
        List<String> collect = Arrays.stream(TransformationType.values()).map(Enum::name).collect(Collectors.toList());
        String[] array = new String[collect.size()];
        for(int i = 0; i < collect.size(); i++) {
            array[i] = collect.get(i);
        }
        return array;
    }
}
