package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

public class TmpCacheWrapper1 {
    private Point y;
    private Point z;
    private double theta;
    private double tx;

    public Point getY() {
        return y;
    }

    public Point getZ() {
        return z;
    }

    public double getTheta() {
        return theta;
    }

    public double getTx() {
        return tx;
    }

    public TmpCacheWrapper1 setY(Point y) {
        this.y = y;
        return this;
    }

    public TmpCacheWrapper1 setZ(Point z) {
        this.z = z;
        return this;
    }

    public TmpCacheWrapper1 setTheta(double theta) {
        this.theta = theta;
        return this;
    }

    public TmpCacheWrapper1 setTx(double tx) {
        this.tx = tx;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TmpCacheWrapper1 that = (TmpCacheWrapper1) o;

        if (Double.compare(that.theta, theta) != 0) return false;
        if (Double.compare(that.tx, tx) != 0) return false;
        if (!y.equals(that.y)) return false;
        return z.equals(that.z);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = y.hashCode();
        result = 31 * result + z.hashCode();
        temp = Double.doubleToLongBits(theta);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tx);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
