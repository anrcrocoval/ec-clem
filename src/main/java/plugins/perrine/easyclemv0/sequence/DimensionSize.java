package plugins.perrine.easyclemv0.sequence;

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
