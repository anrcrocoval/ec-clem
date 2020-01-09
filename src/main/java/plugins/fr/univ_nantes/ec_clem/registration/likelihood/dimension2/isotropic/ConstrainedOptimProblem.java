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
package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic;

import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.BaseOptimProblem;
import org.apache.commons.lang.ArrayUtils;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;

import static java.lang.Math.*;

public class ConstrainedOptimProblem extends BaseOptimProblem {

    public ConstrainedOptimProblem(FiducialSet fiducialSet) {
        super(fiducialSet);
    }

    public int getNConstraints() {
        return 2;
    }

    public int getNonZeroElementsInConstraintJacobian() {
        return 14;
    }

    public double[] getConstraintsLowerBounds() {
        return new double[] {
            0, 0
        };
    }

    public double[] getConstraintsUpperBounds() {
        return new double[] {
            0, 0
        };
    }

    public double[] getConstraints(double[] point) {
        return new double[] {
            getConstraint0(point),
            getConstraint1(point)
        };
    }

    private double getConstraint0(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        return pow(v11, 2) + pow(v21, 2) - pow(v12, 2) - pow(v22, 2);
    }

    private double getConstraint1(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        return v11 * v12 + v22 * v21;
    }

    public double[] getConstraintsJacobian(double[] point) {
        return ArrayUtils.addAll(
            getConstraint0Gradient(point),
            getConstraint1Gradient(point)
        );
    }

    private double[] getConstraint0Gradient(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        return new double[] {
            0, 0, 0, 2d * v11, -2d * v12, 2d * v21, -2d * v22
        };
    }

    private double[] getConstraint1Gradient(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        return new double[] {
            0, 0, 0, v12, v11, v22, v21
        };
    }

    public double[][] getConstraintsHessian(double[] point) {
        return new double[][] {
            getConstraint0Hessian(point),
            getConstraint1Hessian(point)
        };
    }

    private double[] getConstraint0Hessian(double[] point) {
        return new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 2d, 0, 0, 0, 0, -2d, 0, 0, 0, 0, 0, 2d, 0, 0, 0, 0, 0, 0, -2d
        };
    }

    private double[] getConstraint1Hessian(double[] point) {
        return new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0
        };
    }
}
