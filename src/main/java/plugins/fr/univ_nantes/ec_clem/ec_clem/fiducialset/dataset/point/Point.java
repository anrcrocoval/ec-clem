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
package plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset.point;

import Jama.Matrix;

import java.util.Arrays;
import java.util.List;

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

    public Point getNearest(List<Point> list) {
        Point nearest = list.get(0);
        for(int i = 1; i < list.size(); i++) {
            Point current = list.get(i);
            if(this.getDistance(current) < this.getDistance(nearest)) {
                nearest = current;
            }
        }
        return nearest;
    }

    @Override
    public String toString() {
        return "Point{" +
            "coordinates=" + Arrays.deepToString(coordinates.getArray()) +
            '}';
    }
}
