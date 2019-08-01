package plugins.perrine.easyclemv0.test.registration;

import Jama.Matrix;

import javax.inject.Inject;

public class RigidTransformationComputer extends SimilarityTransformationComputer {

    @Inject
    public RigidTransformationComputer() {}

    @Override
    protected Matrix getS(Matrix sourceDataset, Matrix targetDataset, Matrix R) {
        return Matrix.identity(R.getRowDimension(), R.getColumnDimension());
    }
}
