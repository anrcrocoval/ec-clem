package plugins.perrine.easyclemv0.model;

import Jama.Matrix;

import java.util.Arrays;

public class Point {
    private Matrix coordinates;

    public Point(int dimension) {
        coordinates = new Matrix(dimension, 1);
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

    public Matrix getmatrix() {
        return coordinates;
    }

    public double getDistance(Point point) {
        return coordinates.minus(point.getmatrix()).norm2();
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
        return new Point(coordinates.minus(point.getmatrix()));
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
