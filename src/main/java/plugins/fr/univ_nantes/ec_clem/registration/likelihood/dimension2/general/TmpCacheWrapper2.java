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
package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general;

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
