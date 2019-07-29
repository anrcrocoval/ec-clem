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

import Jama.Matrix;

public class KalmanFilterState {
    private Matrix estimate;
    private Matrix covariance;

    public KalmanFilterState(Matrix estimate, Matrix covariance) {
        this.estimate = estimate;
        this.covariance = covariance;
    }

    public Matrix getEstimate() {
        return estimate;
    }

    public Matrix getCovariance() {
        return covariance;
    }
}
