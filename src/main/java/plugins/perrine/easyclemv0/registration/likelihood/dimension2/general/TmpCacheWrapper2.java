package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

import java.util.Arrays;

public class TmpCacheWrapper2 {
    private double[] z;
    private double theta;

    public double[] getZ() {
        return z;
    }

    public double getTheta() {
        return theta;
    }

    public TmpCacheWrapper2 setZ(double[] z) {
        this.z = z;
        return this;
    }

    public TmpCacheWrapper2 setTheta(double theta) {
        this.theta = theta;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TmpCacheWrapper2 that = (TmpCacheWrapper2) o;

        if (Double.compare(that.theta, theta) != 0) return false;
        return Arrays.equals(z, that.z);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = Arrays.hashCode(z);
        temp = Double.doubleToLongBits(theta);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
