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

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;
import java.util.Arrays;
import java.util.concurrent.*;
import static java.lang.Math.*;

public class BaseCovarianceOptimProblem implements OptimProblem {

    protected FiducialSet fiducialSet;
    private Transformation transformation;

    public int getNParameters() {
        return fiducialSet.getSourceDataset().getDimension() * fiducialSet.getTargetDataset().getDimension();
    }

    public int getNConstraints() {
        return 0;
    }

    public int getNonZeroElementsInConstraintJacobian() {
        return 0;
    }

    public int getNonZeroElementsInParametersHessian() {
        return (getNParameters() * (getNParameters() - 1) / 2) + getNParameters();
    }

    public double[] getStartingPoint() {
        return Matrix.identity(fiducialSet.getSourceDataset().getDimension(), fiducialSet.getTargetDataset().getDimension()).getRowPackedCopy();
    }

    private ExecutorService completionService;

    public BaseCovarianceOptimProblem(FiducialSet fiducialSet, Transformation transformation) {
        this.fiducialSet = fiducialSet;
        this.transformation = transformation;
        completionService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void close() {
        completionService.shutdown();
        completionService = null;
    }

    public double getObjectiveValue(double[] point) {
        double sum = 0d;
        Matrix v = new Matrix(point,  fiducialSet.getTargetDataset().getDimension()).transpose();
        Matrix inverseCovariance = v.transpose().times(v);
        Matrix residuals = fiducialSet.getTargetDataset().getMatrix().minus(
            transformation.apply(fiducialSet.getSourceDataset()).getMatrix()
        );
        for(int i = 0; i < residuals.getRowDimension(); i++) {
            Matrix current = residuals.getMatrix(i, i, 0, residuals.getColumnDimension() - 1);
            sum += (current).times(inverseCovariance).times(current.transpose()).get(0, 0);
        }
        return (log(sqrt(inverseCovariance.det()) / (2d * PI)) * residuals.getRowDimension() - sum / 2d) * -1d;
    }

    public double[] getObjectiveGradient(double[] point) {
        Matrix v = new Matrix(point,  fiducialSet.getTargetDataset().getDimension()).transpose();
        Matrix residuals = fiducialSet.getTargetDataset().getMatrix().minus(
            transformation.apply(fiducialSet.getSourceDataset()).getMatrix()
        );
        double[] sum = new double[point.length];
        int count = 0;
        for(int i = 0; i < v.getRowDimension(); i++) {
            for (int j = 0; j < v.getColumnDimension(); j++) {
                for(int n = 0; n < residuals.getRowDimension(); n++) {
                    Matrix current = residuals.getMatrix(n, n, 0, residuals.getColumnDimension() - 1);
                    Matrix xx = current.transpose().times(current);
                    for(int a = 0; a < v.getColumnDimension(); a++) {
                        sum[count] = sum[count] + 2 * v.get(i, a) * xx.get(a, j);
                    }
                }
                count++;
            }
        }

        double[] result = new double[point.length];
        int k = 0;
        for(int i = 0; i < v.getRowDimension(); i++) {
            for (int j = 0; j < v.getColumnDimension(); j++) {
                result[k] = ((fiducialSet.getN() * v.inverse().get(j, i)) - (sum[k] / 2d)) * -1d;
                k++;
            }
        }
        return result;
    }

    protected double getDerivative(Matrix v, int i, int j, int a, int b) {
        if(i == j && i == b) {
            return 2 * v.get(a, b);
        } else if(i == b) {
            return v.get(a, j);
        } else if(j == b) {
            return v.get(a, i);
        }
        return 0;
    }

    public double[] getObjectiveHessian(double[] point) {
        Matrix v = new Matrix(point,  fiducialSet.getTargetDataset().getDimension()).transpose();
        double[] result = new double[getNonZeroElementsInParametersHessian()];
        int h = 0;
        for(int i = 0; i < v.getRowDimension(); i++) {
            for (int j = 0; j < v.getColumnDimension(); j++) {
                for(int k = 0; k <= i; k++) {
                    for (int l = 0; (l <= j && k == i) || (k < i && l < v.getColumnDimension()); l++) {
                        result[h] = (fiducialSet.getN() * v.inverse().get(k, j) * v.inverse().get(i, l));
                        h++;
                    }
                }
            }
        }
        return result;
    }

    public double[] getConstraints(double[] point) {
        return new double[0];
    }

    public double[] getConstraintsJacobian(double[] point) {
        return new double[0];
    }

    public double[][] getConstraintsHessian(double[] point) {
        return new double[0][0];
    }

    public double[] getParametersLowerBounds() {
        double[] array = new double[getNParameters()];
        Arrays.fill(array, -Double.MAX_VALUE);
        return array;
    }

    public double[] getParametersUpperBounds() {
        double[] array = new double[getNParameters()];
        Arrays.fill(array, Double.MAX_VALUE);
        return array;
    }

    public double[] getConstraintsLowerBounds() {
        return new double[0];
    }

    public double[] getConstraintsUpperBounds() {
        return new double[0];
    }
}

