package plugins.perrine.easyclemv0.fiducialset.dataset.point;

import Jama.Matrix;

import java.util.Arrays;

public class Point {
    private Matrix coordinates;

    public Point(int dimension) {
        coordinates = new Matrix(dimension, 1, 0);
    }

    public Point(Matrix matrix) {
        this.coordinates = matrix;
    }

    public Point(double[] coordinates) {
        this.coordinates = new Matrix(coordinates.length, 1);
        for(int i = 0; i < coordinates.length; i++) {
            this.coordinates.set(i, 0, coordinates[i]);
        }
    }

    public int getDimension() {
        return coordinates.getRowDimension();
    }

    public double get(int i) {
        return coordinates.get(i, 0);
    }

    public Matrix getMatrix() {
        return coordinates;
    }

    public double getDistance(Point point) {
        return coordinates.minus(point.getMatrix()).norm2();
    }

    public double getSquareDistance(Matrix axis, Matrix pointOnTheAxis) {
        return new Point(
            coordinates.minus(pointOnTheAxis).minus(
                axis.times(
                    coordinates.transpose().times(axis).get(0, 0) / axis.transpose().times(axis).get(0, 0)
                )
        )).getSumOfSquare();
    }

    public Point minus(Point point) {
        return new Point(coordinates.minus(point.getMatrix()));
    }

    public Point plus(Point point) {
        return new Point(coordinates.plus(point.getMatrix()));
    }

    public double getSumOfSquare() {
        return coordinates.transpose().times(coordinates).get(0, 0);
    }

    @Override
    public String toString() {
        return "Point{" +
            "coordinates=" + Arrays.deepToString(coordinates.getArray()) +
            '}';
    }
}
