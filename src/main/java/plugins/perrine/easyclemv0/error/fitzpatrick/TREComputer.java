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
package plugins.perrine.easyclemv0.error.fitzpatrick;

import Jama.Matrix;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

import javax.inject.Inject;

public class TREComputer {
    private double expectedSquareFLE;
    private double n;
    private double[] f;
    private Matrix eigenVectors;
    private Matrix barycentre;
    private Mean mean = new Mean();

    @Inject
    public TREComputer(double n, double[] f, Matrix eigenVectors, Matrix barycentre, double expectedSquareFLE) {
        this.n = n;
        this.f = f;
        this.eigenVectors = eigenVectors;
        this.barycentre = barycentre;
        this.expectedSquareFLE = expectedSquareFLE;
    }

    public double getN() {
        return n;
    }

    public double[] getF() {
        return f;
    }

    public double getExpectedSquareTRE(Point point) {
        double[] d = new double[point.getDimension()];
        for (int i = 0; i < point.getDimension(); i++) {
            d[i] += point.getSquareDistance(eigenVectors.getMatrix(0, eigenVectors.getRowDimension() - 1, i, i), barycentre);
        }

        mean.clear();
        for (int i = 0; i < point.getDimension(); i++) {
            mean.increment(
                d[i] / f[i]
            );
        }

        return Math.sqrt(
            expectedSquareFLE * ((1 / n) + ((1 / (double) point.getDimension())) * mean.getResult())
        );
    }
}
