package plugins.perrine.easyclemv0.model;

import icy.sequence.DimensionId;

public class DimensionSize {
    private DimensionId dimensionId;
    private int size;
    private double pixelSizeInNanometer;

    public DimensionSize(DimensionId dimensionId, int size, double pixelSizeInNanometer) {
        this.dimensionId = dimensionId;
        this.size = size;
        this.pixelSizeInNanometer = pixelSizeInNanometer;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public int getSize() {
        return size;
    }

    public double getPixelSizeInNanometer() {
        return pixelSizeInNanometer;
    }
}
