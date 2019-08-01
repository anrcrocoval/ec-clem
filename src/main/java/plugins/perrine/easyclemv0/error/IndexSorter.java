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
package plugins.perrine.easyclemv0.error;

import java.util.Arrays;
import java.util.Comparator;

public class IndexSorter<T extends Comparable<T>> {
    private final T[] values;
    private final Integer[] indices;

    private Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer i0, Integer i1) {
            return values[i0].compareTo(values[i1]);
        }
    };

    public IndexSorter(T[] values) {
        this.values = values;
        indices = new Integer[this.values.length];
        for ( int i = 0; i < indices.length; i++ ){
            indices[i] = i;
        }
        Arrays.sort(indices, comparator);
    }

    public Integer[] getIndices(){
        return indices;
    }
}
