package plugins.perrine.easyclemv0.sequence;

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
