package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import java.util.Arrays;

public class CacheWrapper {
    private double[] data;

    public double[] getPoint() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheWrapper that = (CacheWrapper) o;

        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    public CacheWrapper set(double[] point) {
        this.data = point;
        return this;
    }
}
