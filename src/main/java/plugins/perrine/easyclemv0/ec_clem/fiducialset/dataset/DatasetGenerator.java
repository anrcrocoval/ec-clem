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
package plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset;

import Jama.Matrix;
import plugins.perrine.easyclemv0.ec_clem.roi.PointType;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.roi.PointType;

import javax.inject.Inject;
import java.util.Random;

public class DatasetGenerator {

    private Random random = new Random();

    @Inject
    public DatasetGenerator() {
    }

    public Dataset addNoise(Dataset dataset, double fle) {
        Matrix copy = dataset.getMatrix().copy();
        for(int i = 0; i < copy.getRowDimension(); i++) {
            for(int j = 0; j < copy.getColumnDimension(); j++) {
                copy.set(i, j, copy.get(i, j) + random.nextGaussian() * fle);
            }
        }
        return new Dataset(copy, dataset.getPointType());
    }

    public Dataset generate(Point center, int radius, int n) {
        Dataset result = new Dataset(center.getDimension(), PointType.FIDUCIAL);
        for (int i = 0; i < n; i++) {
            Point point = new Point(result.getDimension());
            for(int d = 0; d < point.getDimension(); d++) {
                point.getMatrix().set(d, 0, center.getMatrix().get(d, 0) + (random.nextGaussian() * radius));
            }
            result.addPoint(point);
        }
        return result;
    }
}
