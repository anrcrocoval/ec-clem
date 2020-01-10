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

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.BaseCovarianceOptimProblem;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;

public class ConstrainedCovarianceOptimProblem extends BaseCovarianceOptimProblem {

    public ConstrainedCovarianceOptimProblem(FiducialSet fiducialSet, Transformation transformation) {
        super(fiducialSet, transformation);
    }

    public int getNConstraints() {
        return (fiducialSet.getTargetDataset().getDimension() - 1)
            + (fiducialSet.getTargetDataset().getDimension() * (fiducialSet.getTargetDataset().getDimension() - 1)) / 2;
    }

    public int getNonZeroElementsInConstraintJacobian() {
        return getNConstraints() * getNParameters();
    }

    public double[] getConstraintsLowerBounds() {
        return new double[getNConstraints()];
    }

    public double[] getConstraintsUpperBounds() {
        return new double[getNConstraints()];
    }

    public double[] getConstraints(double[] point) {
        Matrix v = new Matrix(point,  fiducialSet.getTargetDataset().getDimension()).transpose();
        Matrix lambdaInv = v.transpose().times(v);
        double[] constraints = new double[getNConstraints()];
        int k = 0;
        for(; k < fiducialSet.getTargetDataset().getDimension() - 1; k++) {
            constraints[k] = lambdaInv.get(0, 0) - lambdaInv.get(k + 1, k + 1);
        }
        for(int i = 0; i < fiducialSet.getTargetDataset().getDimension(); i++) {
            for(int j = 0; j < i; j++) {
                constraints[k] = lambdaInv.get(i, j);
                k++;
            }
        }
        return constraints;
    }

    public double[] getConstraintsJacobian(double[] point) {
        double[] derivatives = new double[getNonZeroElementsInConstraintJacobian()];
        Matrix v = new Matrix(point,  fiducialSet.getTargetDataset().getDimension()).transpose();
        for(int k = 0; k < fiducialSet.getTargetDataset().getDimension() - 1; k++) {
            int count = 0;
            for(int a = 0; a < v.getRowDimension(); a++) {
                for(int b = 0; b < v.getColumnDimension(); b++) {
                    derivatives[k * point.length + count] = getDerivative(v, 0, 0, a, b) - getDerivative(v, k + 1, k + 1, a, b);
                    count++;
                }
            }
        }
        int start = (fiducialSet.getTargetDataset().getDimension() - 1) * point.length;
        for(int i = 0; i < v.getRowDimension(); i++) {
            for(int j = 0; j < i; j++) {
                int count = 0;
                for(int a = 0; a < v.getRowDimension(); a++) {
                    for(int b = 0; b < v.getColumnDimension(); b++) {
                        derivatives[start + count] = getDerivative(v, i, j, a, b);
                        count++;
                    }
                }
                start += point.length;
            }
        }
        return derivatives;
    }

    public double[][] getConstraintsHessian(double[] point) {
        double[][] derivatives = new double[getNConstraints()][(getNParameters() * (getNParameters() - 1) / 2) + getNParameters()];
        Matrix v = new Matrix(point,  fiducialSet.getTargetDataset().getDimension()).transpose();
        for(int k = 0; k < fiducialSet.getTargetDataset().getDimension() - 1; k++) {
            int count = 0;
            for(int a = 0; a < v.getRowDimension(); a++) {
                for(int b = 0; b < v.getColumnDimension(); b++) {
                    for(int c = 0; c <= a; c++) {
                        for(int d = 0; (d <= b && c == a) || (c < a && d < v.getColumnDimension()); d++) {
                            derivatives[k][count] = getSecondDerivative(v, 0, 0, a, b, c, d) - getSecondDerivative(v, k + 1, k + 1, a, b, c, d);
                            count++;
                        }
                    }
                }
            }
        }

        int start = (fiducialSet.getTargetDataset().getDimension() - 1);
        for(int i = 0; i < v.getRowDimension(); i++) {
            for(int j = 0; j < i; j++) {
                int count = 0;
                for(int a = 0; a < v.getRowDimension(); a++) {
                    for(int b = 0; b < v.getColumnDimension(); b++) {
                        for(int c = 0; c <= a; c++) {
                            for (int d = 0; (d <= b && c == a) || (c < a && d < v.getColumnDimension()); d++) {
                                derivatives[start][count] = getSecondDerivative(v, i, j, a, b, c, d);
                                count++;
                            }
                        }
                    }
                }
                start++;
            }
        }
        return derivatives;
    }

    private double getSecondDerivative(Matrix v, int i, int j, int a, int b, int c, int d) {
        if(c == a) {
            if(i == j && i == b && b == d) {
                return 2;
            } else if(i == b && j == d) {
                return 1;
            } else if(j == b && i == d) {
                return 1;
            }
        }
        return 0;
    }
}
