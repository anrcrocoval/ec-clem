package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;

public class RigidTransformationComputer extends SimilarityTransformationComputer {

    @Override
    protected Matrix getS(Matrix sourceDataset, Matrix targetDataset, Matrix R) {
        return Matrix.identity(R.getRowDimension(), R.getColumnDimension());
    }
}
