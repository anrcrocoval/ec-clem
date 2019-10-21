package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import Jama.Matrix;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Rigid2DIsotropicMaxLikelihoodObjectiveFunctionGradient implements MultivariateVectorFunction {

    private MatrixUtil matrixUtil;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private FiducialSet fiducialSet;
    private Matrix sigmaInv;
    private MultivariateFunctionMappingAdapter adapter;

    public Rigid2DIsotropicMaxLikelihoodObjectiveFunctionGradient(FiducialSet fiducialSet, MultivariateFunctionMappingAdapter adapter) {
        DaggerRigid2DIsotropicLikelihoodObjectiveFunctionGradientComponent.create().inject(this);
        this.fiducialSet = fiducialSet;
        sigmaInv = matrixUtil.pseudoInverse(covarianceMatrixComputer.compute(fiducialSet.getTargetDataset().getMatrix()));
        this.adapter = adapter;
    }

//    @Override
//    public double[] value(double[] point) throws IllegalArgumentException {
//        if(point.length != 4) {
//            throw new DimensionMismatchException(point.length, 4);
//        }
//
//        double bounded[] = adapter.unboundedToBounded(point);
//
//        Matrix R = getR(bounded[2]);
//        Matrix R2 = getR2(bounded[2]);
//        Matrix T = getT(bounded[0], bounded[1]);
//        Matrix partialT = new Matrix(2, 1, 0);
//        Matrix partialTheta = new Matrix(1, 1, 0);
//        double partialSigma = 0;
//        for(int i = 0; i < fiducialSet.getN(); i++) {
//            Matrix y = fiducialSet.getTargetDataset().getPoint(i).getMatrix();
//            Matrix z = fiducialSet.getSourceDataset().getPoint(i).getMatrix();
//            Matrix tmp = y.minus(R.times(z)).minus(T);
//            partialT.plusEquals(getSigmaInv(bounded).times(tmp));
//            partialTheta.plusEquals(R2.times(z).transpose().times(getSigmaInv(bounded)).times(tmp).times(-1));
//            partialSigma += (Math.pow(tmp.get(0, 0), 2) + Math.pow(tmp.get(1, 0), 2)) * -1/2;
//        }
//        return new double[] {
//                partialT.get(0, 0),
//                partialT.get(1, 0),
//                partialTheta.get(0, 0),
//                partialSigma,
//        };
//    }

    @Override
    public double[] value(double[] point) throws IllegalArgumentException {
        if(point.length != 4) {
            throw new DimensionMismatchException(point.length, 4);
        }

        DerivativeStructure tx = new DerivativeStructure(6, 1, 0, point[0]);
        DerivativeStructure ty = new DerivativeStructure(6, 1, 1, point[1]);
        DerivativeStructure theta = new DerivativeStructure(6, 1, 2, point[2]);
        DerivativeStructure lambda11 = new DerivativeStructure(6, 1, 3, point[3]);

        DerivativeStructure sigmoid_theta = theta.negate().exp().add(1).reciprocal().multiply(2 * Math.PI);
        DerivativeStructure exp_lambda11 = lambda11.abs().add(0.00000001);

        DerivativeStructure sum = null;

        for(int i = 0; i < fiducialSet.getN(); i++) {
            Point y = fiducialSet.getTargetDataset().getPoint(i);
            Point z = fiducialSet.getSourceDataset().getPoint(i);
            DerivativeStructure tmp1 = tx.negate()
                    .add(y.get(0))
                    .subtract(sigmoid_theta.cos().multiply(z.get(0)))
                    .add(sigmoid_theta.sin().multiply(z.get(1)));
            DerivativeStructure tmp2 = ty.negate()
                    .add(y.get(1))
                    .subtract(sigmoid_theta.sin().multiply(z.get(0)))
                    .subtract(sigmoid_theta.cos().multiply(z.get(1)));
            DerivativeStructure sum_i = tmp1.multiply(tmp1.multiply(exp_lambda11))
                    .add(tmp2.multiply(tmp2.multiply(exp_lambda11)));

            if(sum == null) {
                sum = sum_i;
            } else {
                sum.add(sum_i);
            }
        }

        return sum.getAllDerivatives();
    }

//    private Matrix getSigmaInv() {
//        return sigmaInv;
//    }

    private Matrix getSigmaInv(double[] point) {
        return new Matrix(new double[][] {
            { point[3], 0 },
            { 0, point[3] }
        });
    }

    private Matrix getR(double theta) {
        return new Matrix(new double[][] {
                { cos(theta), -sin(theta) },
                { sin(theta), cos(theta) }
        });
    }

    private Matrix getR2(double theta) {
        return new Matrix(new double[][] {
                { sin(theta), cos(theta) },
                { -cos(theta), sin(theta) }
        });
    }

    private Matrix getT(double tx, double ty) {
        return new Matrix(new double[][] {
                { tx },
                { ty }
        });
    }

    @Inject
    public void setCovarianceMatrixComputer(CovarianceMatrixComputer covarianceMatrixComputer) {
        this.covarianceMatrixComputer = covarianceMatrixComputer;
    }

    @Inject
    public void setMatrixUtil(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }
}
