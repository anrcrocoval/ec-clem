package plugins.perrine.easyclemv0.test.registration;

import Jama.Matrix;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.transformation.AffineTransformation;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;

import javax.inject.Inject;

public class AffineTransformationComputer implements TransformationComputer {

    private MatrixUtil matrixUtil;

    @Inject
    public AffineTransformationComputer(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    @Override
    public AffineTransformation compute(FiducialSet fiducialSet) {
        Matrix A = fiducialSet.getSourceDataset().getHomogeneousMatrixLeft();
        Matrix B = fiducialSet.getTargetDataset().getMatrix();
        Matrix result = matrixUtil.pseudoInverse(A.transpose().times(A)).times(A.transpose()).times(B).transpose();
        return new AffineTransformation(
            result.getMatrix(0, result.getRowDimension() - 1, 1, result.getColumnDimension() - 1),
            result.getMatrix(0, result.getRowDimension() - 1, 0, 0)
        );
    }
}
