package plugins.perrine.easyclemv0.util;

import Jama.Matrix;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;

import java.util.Random;

public class DatasetGenerator {

    private Random random = new Random();

    public Dataset addNoise(Dataset dataset, double fle) {
        Matrix copy = dataset.getMatrix().copy();
        for(int i = 0; i < copy.getRowDimension(); i++) {
            for(int j = 0; j < copy.getColumnDimension(); j++) {
                copy.set(i, j, copy.get(i, j) + random.nextGaussian() * fle);
            }
        }
        return new Dataset(copy);
    }

    public Dataset generate(Point center, int radius, int n) {
        Dataset result = new Dataset(center.getDimension());
        for (int i = 0; i < n; i++) {
            Point point = new Point(result.getDimension());
            for(int d = 0; d < point.getDimension(); d++) {
                point.getmatrix().set(d, 0, center.getmatrix().get(d, 0) + (random.nextGaussian() * radius));
            }
            result.addPoint(point);
        }
        return result;
    }
}
