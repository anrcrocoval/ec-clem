package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.transformation.Similarity;

import static java.lang.Math.max;

public class RigidTransformationComputer implements TransformationComputer {

    public Similarity compute(FiducialSet fiducialSet) {
        return compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }

    private Similarity compute(Dataset source, Dataset target) {
        if (source.getN() < 2) {
            int dimension = max(source.getDimension(), target.getDimension());
            return new Similarity(
                    Matrix.identity(
                            dimension,
                            dimension
                    ),
                    new Matrix(dimension, 1, 0),
                    1
            );
        }

        Dataset clonedSourceDataset = source.clone();
        Dataset clonedTargetDataset = target.clone();
        Point sourceBarycentre = clonedSourceDataset.getBarycentre();
        Point targetBarycentre = clonedTargetDataset.getBarycentre();

        clonedSourceDataset.substractBarycentre();
        clonedTargetDataset.substractBarycentre();

        double scale = clonedTargetDataset.getMeanNorm() / clonedSourceDataset.getMeanNorm();
        Matrix R = getR(clonedSourceDataset, clonedTargetDataset);
        Matrix T = getT(sourceBarycentre.getMatrix(), targetBarycentre.getMatrix(), R, scale);
        return new Similarity(R, T, scale);
    }

    private Matrix getR(Dataset source, Dataset target) {
        Matrix S = target.getMatrix().transpose().times(source.getMatrix());
        SingularValueDecomposition svd = S.svd();
        return svd.getU().times(svd.getV().transpose());
    }

    private Matrix getT(Matrix sourceBarycentre, Matrix targetBarycentre, Matrix R, double scale) {
        return targetBarycentre.minus(R.times(scale).times(sourceBarycentre));
    }
}
