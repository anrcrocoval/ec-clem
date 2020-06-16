package plugins.fr.univ_nantes.ec_clem.ec_clem.error.ellipse;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset.point.Point;

public class Ellipse {
    private double[] eigenValues;
    private Matrix eigenVectors;
    private Point center;

    public Ellipse(double[] eigenValues, Matrix eigenVectors, Point center) {
        this.eigenValues = eigenValues;
        this.eigenVectors = eigenVectors;
        this.center = center;
    }

    public double[] getEigenValues() {
        return eigenValues;
    }

    public Matrix getEigenVectors() {
        return eigenVectors;
    }

    public Point getCenter() {
        return center;
    }
}
