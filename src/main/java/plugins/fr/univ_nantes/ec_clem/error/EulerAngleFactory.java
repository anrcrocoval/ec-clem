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
package plugins.fr.univ_nantes.ec_clem.error;

import Jama.Matrix;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

import javax.inject.Inject;

public class EulerAngleFactory {

    @Inject
    public EulerAngleFactory() {
    }

    public double[] getFrom(Matrix M) {
        Rotation rotation = new Rotation(M.getArray(), 0.01);
        return rotation.getAngles(RotationOrder.ZYZ, RotationConvention.VECTOR_OPERATOR);
    }
}
