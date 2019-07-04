package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import plugins.perrine.easyclemv0.model.Point;

public class TREComputer {
    private double expectedSquareFLE;
    private double n;
    private double[] f;
    private Matrix eigenVectors;
    private Matrix barycentre;
    private Mean mean = new Mean();

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
