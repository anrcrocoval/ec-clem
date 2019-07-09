package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.transformation.AffineTransformation;
import plugins.perrine.easyclemv0.util.MatrixUtil;

public class AffineTransformationComputer implements TransformationComputer {

    private MatrixUtil matrixUtil = new MatrixUtil();

    @Override
    public AffineTransformation compute(FiducialSet fiducialSet) {
        Matrix A = fiducialSet.getSourceDataset().getHomogeneousMatrix2();
        Matrix B = fiducialSet.getTargetDataset().getMatrix();
        Matrix result = matrixUtil.pseudoInverse(A.transpose().times(A)).times(A.transpose()).times(B).transpose();
        return new AffineTransformation(
            result.getMatrix(0, result.getRowDimension() - 1, 1, result.getColumnDimension() - 1),
            result.getMatrix(0, result.getRowDimension() - 1, 0, 0)
        );
    }
}
