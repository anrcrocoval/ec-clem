package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import Jama.Matrix;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import javax.inject.Inject;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Rigid2DMaxLikelihoodObjectiveFunctionGradient implements MultivariateVectorFunction {

    private CovarianceMatrixComputer covarianceMatrixComputer;
    private MatrixUtil matrixUtil;

    private FiducialSet fiducialSet;
    private Matrix sigmaInv;

    public Rigid2DMaxLikelihoodObjectiveFunctionGradient(FiducialSet fiducialSet) {
        DaggerRigid2DLikelihoodObjectiveFunctionGradientComponent.create().inject(this);
        this.fiducialSet = fiducialSet;
        sigmaInv = matrixUtil.pseudoInverse(
            covarianceMatrixComputer.compute(fiducialSet.getTargetDataset().getMatrix())
        );
    }

    @Override
    public double[] value(double[] point) throws IllegalArgumentException {
        if(point.length != 3) {
            throw new DimensionMismatchException(point.length, 3);
        }
        Matrix R = getR(point[2]);
        Matrix R2 = getR2(point[2]);
        Matrix T = getT(point[0], point[1]);
        Matrix partialT = new Matrix(2, 1, 0);
        Matrix partialTheta = new Matrix(1, 1, 0);
        for(int i = 0; i < fiducialSet.getN(); i++) {
            Matrix y = fiducialSet.getTargetDataset().getPoint(i).getMatrix();
            Matrix z = fiducialSet.getSourceDataset().getPoint(i).getMatrix();
            Matrix tmp = y.minus(R.times(z)).minus(T);
            partialT.plusEquals(sigmaInv.times(tmp).times(-2));
            partialTheta.plusEquals(R2.times(z).transpose().times(sigmaInv).times(tmp).times(2));
        }
        return new double[] {partialT.get(0, 0), partialT.get(1, 0), partialTheta.get(0, 0)};
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
