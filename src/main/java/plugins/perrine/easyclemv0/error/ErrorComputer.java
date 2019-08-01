package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import plugins.perrine.easyclemv0.factory.TransformationFactory;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.TransformationSchema;
import plugins.perrine.easyclemv0.model.transformation.Similarity;
import plugins.perrine.easyclemv0.model.transformation.SplineTransformation;
import plugins.perrine.easyclemv0.model.transformation.Transformation;
import plugins.perrine.easyclemv0.util.MatrixUtil;

import javax.inject.Inject;

public class ErrorComputer {

    private TransformationFactory transformationFactory;
    private MatrixUtil matrixUtil;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private EulerAngleFactory eulerAngleFactory;

    @Inject
    public ErrorComputer(TransformationFactory transformationFactory, MatrixUtil matrixUtil, CovarianceMatrixComputer covarianceMatrixComputer, EulerAngleFactory eulerAngleFactory) {
        this.transformationFactory = transformationFactory;
        this.matrixUtil = matrixUtil;
        this.covarianceMatrixComputer = covarianceMatrixComputer;
        this.eulerAngleFactory = eulerAngleFactory;
    }

    public Matrix getCovarianceEstimate(TransformationSchema transformationSchema) {
        Transformation from = transformationFactory.getFrom(transformationSchema);
        if(from instanceof SplineTransformation) {
            throw new RuntimeException("Error estimation for SplineTransformation is not supported");
        }

        Similarity similarity = (Similarity) from;
        Matrix r = similarity.getR();
        Matrix t = similarity.getT();
        Matrix X = new Matrix(
            transformationSchema.getFiducialSet().getN(),
            transformationSchema.getFiducialSet().getSourceDataset().getDimension() + transformationSchema.getFiducialSet().getTargetDataset().getDimension()
        );

        X.setMatrix(
                0, transformationSchema.getFiducialSet().getN() - 1,
                0, transformationSchema.getFiducialSet().getTargetDataset().getDimension() - 1,
                transformationSchema.getFiducialSet().getTargetDataset().getMatrix()
        );

        X.setMatrix(
                0, transformationSchema.getFiducialSet().getN() - 1,
                transformationSchema.getFiducialSet().getTargetDataset().getDimension(), transformationSchema.getFiducialSet().getTargetDataset().getDimension() + transformationSchema.getFiducialSet().getSourceDataset().getDimension() - 1,
                transformationSchema.getFiducialSet().getSourceDataset().getMatrix()
        );

        Matrix cov = covarianceMatrixComputer.compute(X);
        Matrix M = getDerivativeMatrix(
                transformationSchema.getFiducialSet(),
                r.get(0,0),
                r.get(0,1),
                r.get(0,2),
                r.get(1,0),
                r.get(1,1),
                r.get(1,2),
                r.get(2,0),
                r.get(2,1),
                r.get(2,2),
                t.get(0, 0),
                t.get(1, 0),
                t.get(2, 0)
        );
        return M.times(cov).times(M.transpose());
    }

    private Matrix getDerivativeMatrix(
            FiducialSet fiducialSet,
            double var_a,
            double var_b,
            double var_c,
            double var_d,
            double var_e,
            double var_f,
            double var_g,
            double var_h,
            double var_i,
            double var_tx,
            double var_ty,
            double var_tz
    ) {
        int[] derivativeIndex = new int[18];
        Matrix M = new Matrix(12, 12, 0);
        Matrix G = new Matrix(12, 6, 0);

        DerivativeStructure y0 = null;
        DerivativeStructure y1 = null;
        DerivativeStructure y2 = null;
        DerivativeStructure z0 = null;
        DerivativeStructure z1 = null;
        DerivativeStructure z2 = null;
        DerivativeStructure a = new DerivativeStructure(18, 2, 6, var_a);
        DerivativeStructure b = new DerivativeStructure(18, 2, 7, var_b);
        DerivativeStructure c = new DerivativeStructure(18, 2, 8, var_c);
        DerivativeStructure d = new DerivativeStructure(18, 2, 9, var_d);
        DerivativeStructure e = new DerivativeStructure(18, 2, 10, var_e);
        DerivativeStructure f = new DerivativeStructure(18, 2, 11, var_f);
        DerivativeStructure g = new DerivativeStructure(18, 2, 12, var_g);
        DerivativeStructure h = new DerivativeStructure(18, 2, 13, var_h);
        DerivativeStructure i = new DerivativeStructure(18, 2, 14, var_i);
        DerivativeStructure tx = new DerivativeStructure(18, 2, 15, var_tx);
        DerivativeStructure ty = new DerivativeStructure(18, 2, 16, var_ty);
        DerivativeStructure tz = new DerivativeStructure(18, 2, 17, var_tz);

        for(int n = 0; n < fiducialSet.getN(); n++) {
            y0 = new DerivativeStructure(18, 2, 0, fiducialSet.getTargetDataset().getPoint(n).get(0));
            y1 = new DerivativeStructure(18, 2, 1, fiducialSet.getTargetDataset().getPoint(n).get(1));
            y2 = new DerivativeStructure(18, 2, 2, fiducialSet.getTargetDataset().getPoint(n).get(2));
            z0 = new DerivativeStructure(18, 2, 3, fiducialSet.getSourceDataset().getPoint(n).get(0));
            z1 = new DerivativeStructure(18, 2, 4, fiducialSet.getSourceDataset().getPoint(n).get(1));
            z2 = new DerivativeStructure(18, 2, 5, fiducialSet.getSourceDataset().getPoint(n).get(2));

            DerivativeStructure F = y0.subtract(
                    a.multiply(z0).add(b.multiply(z1)).add(c.multiply(z2)).add(tx)
            ).pow(2).add(
                    y1.subtract(
                            d.multiply(z0).add(e.multiply(z1)).add(f.multiply(z2)).add(ty)
                    ).pow(2)
            ).add(
                    y2.subtract(
                            g.multiply(z0).add(h.multiply(z1)).add(i.multiply(z2)).add(tz)
                    ).pow(2)
            );

            for(int j = 0; j < 12; j++) {
                for(int k = 0; k < 12; k++) {
                    derivativeIndex[6 + j] += 1;
                    derivativeIndex[6 + k] += 1;
                    M.set(j, k, M.get(j, k) + F.getPartialDerivative(derivativeIndex));
                    derivativeIndex[6 + j] -= 1;
                    derivativeIndex[6 + k] -= 1;
                }
            }

            for(int j = 0; j < 12; j++) {
                for(int k = 0; k < 6; k++) {
                    derivativeIndex[6 + j] += 1;
                    derivativeIndex[k] += 1;
                    G.set(j, k, G.get(j, k) + F.getPartialDerivative(derivativeIndex));
                    derivativeIndex[6 + j] -= 1;
                    derivativeIndex[k] -= 1;
                }
            }
        }

        return matrixUtil.pseudoInverse(M).times(G);
    }
}
