package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.util.CombinatoricsUtils;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

import javax.inject.Inject;
import java.util.Iterator;

public class InertiaMatrixComputer {

    @Inject
    public InertiaMatrixComputer() {
    }

    public Matrix getInertiaMatrix(Dataset dataset) {
        Matrix M = new Matrix(dataset.getDimension(), dataset.getDimension());
        for(int i = 0; i < dataset.getDimension(); i++) {
            M.set(i, i, moment(dataset, i));
        }
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(dataset.getDimension(), 2);
        iterator.forEachRemaining((int[] element) -> {
            double value = product(dataset, element[0], element[1]) * -1;
            M.set(element[0], element[1], value);
            M.set(element[1], element[0], value);
        });
        return M;
    }

    private double product(Dataset dataset, int col1, int col2) {
        double result = 0;
        for (int i = 0; i < dataset.getN(); i++) {
            result += dataset.getMatrix().get(i, col1) * dataset.getMatrix().get(i, col2);
        }
        return result;
    }

    private double moment(Dataset dataset, int col) {
        double result = 0;
        Matrix u = new Matrix(dataset.getDimension(), 1);
        u.set(col, 0, 1);

        Point point = new Point(dataset.getDimension());
        for (int i = 0; i < dataset.getN(); i++) {
            for (int j = 0; j < dataset.getDimension(); j++) {
                point.getMatrix().set(j, 0, dataset.getMatrix().get(i, j));
            }
            result += point.getSquareDistance(u, new Matrix(dataset.getDimension(), 1, 0));
        }
        return result;
    }
}
