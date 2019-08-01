package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Point;

import javax.inject.Inject;

public class ExtendedKalmanFilter {

    private CovarianceMatrixComputer covarianceMatrixComputer;

    @Inject
    public ExtendedKalmanFilter(CovarianceMatrixComputer covarianceMatrixComputer) {
        this.covarianceMatrixComputer = covarianceMatrixComputer;
    }

    public KalmanFilterState run(FiducialSet fiducialSet, Matrix estimate, Matrix covariance) {
        Matrix g = estimate;
        Matrix G = covariance;

        Matrix residuals = fiducialSet.getTargetDataset().getMatrix().minus(
                g.times(fiducialSet.getSourceDataset().getHomogeneousMatrixRight().transpose()).transpose()
        );
        Matrix estimatedNoiseOnFeatures = covarianceMatrixComputer.compute(residuals).timesEquals(1 / (float) 2);
        Dataset sourceDataset = fiducialSet.getSourceDataset();
        Dataset targetDataset = fiducialSet.getTargetDataset();
        Double[] mahalanobis = new Double[fiducialSet.getN()];
        for(int i = 0; i < fiducialSet.getN(); i++) {
            mahalanobis[i] = Math.sqrt(
                    targetDataset.getPoint(i).minus(sourceDataset.getPoint(i)).getMatrix().transpose().times(
                            estimatedNoiseOnFeatures
                    ).times(
                            targetDataset.getPoint(i).minus(sourceDataset.getPoint(i)).getMatrix()
                    ).get(0, 0)
            );
        }
        IndexSorter<Double> indexSorter = new IndexSorter<>(mahalanobis);
        fiducialSet.sort(indexSorter.getIndices());

        for(int i = 0; i < fiducialSet.getN(); i++) {
            Matrix M = getDerivativeMatrix(fiducialSet.getSourceDataset().getPoint(i), g);
            residuals = fiducialSet.getTargetDataset().getMatrix().minus(
                    g.times(fiducialSet.getSourceDataset().getHomogeneousMatrixRight().transpose()).transpose()
            );
            Matrix covzz = residuals.transpose().times(residuals).timesEquals(1 / (float) fiducialSet.getN());
            Matrix K = G.times(M.transpose()).times(covzz.plus(M.times(G).times(M.transpose())).inverse());
            g = g.minus(new Matrix(K.times(residuals.getMatrix(i, i, 0, residuals.getColumnDimension() - 1).transpose()).getColumnPackedCopy(), 3));
            G = Matrix.identity(12,12).minus(K.times(M)).times(G);
        }
        return new KalmanFilterState(g, G);
    }

    private Matrix getDerivativeMatrix(Point z, Matrix state) {
        int[] derivativeIndex = new int[15];
        Matrix M = new Matrix(3, 12, 0);

        DerivativeStructure a = new DerivativeStructure(15, 2, 0, state.get(0, 0));
        DerivativeStructure b = new DerivativeStructure(15, 2, 1, state.get(0, 1));
        DerivativeStructure c = new DerivativeStructure(15, 2, 2, state.get(0, 2));
        DerivativeStructure d = new DerivativeStructure(15, 2, 3, state.get(1, 0));
        DerivativeStructure e = new DerivativeStructure(15, 2, 4, state.get(1, 1));
        DerivativeStructure f = new DerivativeStructure(15, 2, 5, state.get(1, 2));
        DerivativeStructure g = new DerivativeStructure(15, 2, 6, state.get(2, 0));
        DerivativeStructure h = new DerivativeStructure(15, 2, 7, state.get(2, 1));
        DerivativeStructure i = new DerivativeStructure(15, 2, 8, state.get(2, 2));
        DerivativeStructure tx = new DerivativeStructure(15, 2, 9, state.get(0, 3));
        DerivativeStructure ty = new DerivativeStructure(15, 2, 10, state.get(1, 3));
        DerivativeStructure tz = new DerivativeStructure(15, 2, 11, state.get(2, 3));
        DerivativeStructure z0 = new DerivativeStructure(15, 2, 12, z.get(0));
        DerivativeStructure z1 = new DerivativeStructure(15, 2, 13, z.get(1));
        DerivativeStructure z2 = new DerivativeStructure(15, 2, 14, z.get(2));

        DerivativeStructure F0 = a.multiply(z0).add(b.multiply(z1)).add(c.multiply(z2)).add(tx);
        DerivativeStructure F1 = d.multiply(z0).add(e.multiply(z1)).add(f.multiply(z2)).add(ty);
        DerivativeStructure F2 = g.multiply(z0).add(h.multiply(z1)).add(i.multiply(z2)).add(tz);

        for(int j = 0; j < 12; j++) {
            derivativeIndex[j] += 1;
            M.set(0, j, F0.getPartialDerivative(derivativeIndex));
            M.set(1, j, F1.getPartialDerivative(derivativeIndex));
            M.set(2, j, F2.getPartialDerivative(derivativeIndex));
            derivativeIndex[j] -= 1;
        }

        return M.timesEquals(-1);
    }
}
