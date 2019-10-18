package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import Jama.Matrix;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient implements MultivariateVectorFunction {

    private FiducialSet fiducialSet;
    private MatrixUtil matrixUtil;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private Matrix sigmaInv;
    private MultivariateFunctionMappingAdapter adapter;

    public Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient(FiducialSet fiducialSet, MultivariateFunctionMappingAdapter adapter) {
        DaggerRigid2DGeneralLikelihoodObjectiveFunctionGradientComponent.create().inject(this);
        this.fiducialSet = fiducialSet;
        sigmaInv = matrixUtil.pseudoInverse(covarianceMatrixComputer.compute(fiducialSet.getTargetDataset().getMatrix()));
        this.adapter = adapter;
    }

    @Override
    public double[] value(double[] point) throws IllegalArgumentException {
        if(point.length != 6) {
            throw new DimensionMismatchException(point.length, 6);
        }

//        double[] bounded = adapter.unboundedToBounded(point);
        double[] bounded = point;

        Matrix R = getR(bounded[2]);
        Matrix R2 = getR2(bounded[2]);
        Matrix T = getT(bounded[0], bounded[1]);
        Matrix partialT = new Matrix(2, 1, 0);
        Matrix partialTheta = new Matrix(1, 1, 0);
        double partialSigma11 = 0;
        double partialSigma12 = 0;
        double partialSigma22 = 0;
        for(int i = 0; i < fiducialSet.getN(); i++) {
            Matrix y = fiducialSet.getTargetDataset().getPoint(i).getMatrix();
            Matrix z = fiducialSet.getSourceDataset().getPoint(i).getMatrix();
            Matrix tmp = y.minus(R.times(z)).minus(T);
            partialT.plusEquals(getSigmaInv(bounded).times(tmp));
            partialTheta.plusEquals(R2.times(z).transpose().times(getSigmaInv(bounded)).times(tmp).times(-1));
            partialSigma11 += Math.pow(tmp.get(0, 0), 2) * -1/2;
            partialSigma12 += tmp.get(0, 0) * tmp.get(1, 0) * -1;
            partialSigma22 += Math.pow(tmp.get(1, 0), 2) * -1/2;
        }
        return new double[] {
            partialT.get(0, 0),
            partialT.get(1, 0),
            partialTheta.get(0, 0),
            partialSigma11,
            partialSigma12,
            partialSigma22
        };
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

//    private Matrix getSigmaInv() {
//        return sigmaInv;
//    }

    private Matrix getSigmaInv(double[] point) {
        return new Matrix(new double[][] {
            { point[3], point[4] },
            { point[4], point[5] }
        });
    }

    @Inject
    public void setMatrixUtil(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    @Inject
    public void setCovarianceMatrixComputer(CovarianceMatrixComputer covarianceMatrixComputer) {
        this.covarianceMatrixComputer = covarianceMatrixComputer;
    }
}
