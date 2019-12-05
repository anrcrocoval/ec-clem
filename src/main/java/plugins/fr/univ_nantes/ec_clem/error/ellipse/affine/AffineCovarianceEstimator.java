package plugins.fr.univ_nantes.ec_clem.error.ellipse.affine;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.error.ellipse.CovarianceEstimator;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.TransformationComputer;
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;
import plugins.fr.univ_nantes.ec_clem.transformation.TransformationFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;

import javax.inject.Inject;

public class AffineCovarianceEstimator implements CovarianceEstimator {

    private MatrixUtil matrixUtil;
    private TransformationFactory transformationFactory;

    @Inject
    public AffineCovarianceEstimator(TransformationFactory transformationFactory, MatrixUtil matrixUtil) {
        this.transformationFactory = transformationFactory;
        this.matrixUtil = matrixUtil;
    }

    @Override
    public Matrix getCovariance(TransformationSchema transformationSchema, Point zSource) {
        double coeff = (
            zSource.getMatrix().transpose().times(
                matrixUtil.pseudoInverse(
                    transformationSchema.getFiducialSet().getSourceDataset().getMatrix().transpose().times(
                        transformationSchema.getFiducialSet().getSourceDataset().getMatrix()
                    )
                )
            ).times(zSource.getMatrix()).get(0, 0) + 1
        );

        Matrix residuals = transformationSchema.getFiducialSet().getTargetDataset().getMatrix().minus(
            transformationFactory.getFrom(transformationSchema).apply(transformationSchema.getFiducialSet().getSourceDataset()).getMatrix()
        );

        return residuals.transpose().times(residuals)
            .times((double) 1 / (
                transformationSchema.getFiducialSet().getN()
                - transformationSchema.getFiducialSet().getSourceDataset().getDimension()
                - transformationSchema.getFiducialSet().getSourceDataset().getDimension())
            ).times(coeff);
    }
}
