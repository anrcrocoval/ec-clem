package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.Similarity;

import static java.lang.Math.max;

public class NDimensionnalSimilarityRegistration {

    public Similarity apply(Dataset source, Dataset target) {
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

        double scale = Math.sqrt(clonedTargetDataset.getMeanNorm() / clonedSourceDataset.getMeanNorm());
        Matrix R = getR(clonedSourceDataset, clonedTargetDataset);
        Matrix T = getT(sourceBarycentre.getMatrix(), targetBarycentre.getMatrix(), R, scale);
        print("R", R);
        print("T", T);
        System.out.println("Scale is " + scale);
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

    private void print(String name, Matrix M) {
        System.out.println(String.format("%s is :", name));
        M.print(1, 5);
    }
}
