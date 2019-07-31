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
