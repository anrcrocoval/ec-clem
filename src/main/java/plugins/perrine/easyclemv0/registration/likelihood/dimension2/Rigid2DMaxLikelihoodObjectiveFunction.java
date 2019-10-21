package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import Jama.Matrix;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;

import javax.inject.Inject;
import java.util.Arrays;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public abstract class Rigid2DMaxLikelihoodObjectiveFunction implements MultivariateFunction {

    protected FiducialSet fiducialSet;
    protected Matrix sigmaInv;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private MatrixUtil matrixUtil;

    public Rigid2DMaxLikelihoodObjectiveFunction(FiducialSet fiducialSet) {
        DaggerRigid2DLikelihoodObjectiveFunctionComponent.create().inject(this);
        this.fiducialSet = fiducialSet;
        sigmaInv = matrixUtil.pseudoInverse(covarianceMatrixComputer.compute(fiducialSet.getTargetDataset().getMatrix()));
    }

//    @Override
//    public double value(double[] point) {
//        checkParameters(point);
//        Matrix R = getR(point[2]);
//        Matrix T = getT(point[0], point[1]);
//        double sum = 0;
//        for(int i = 0; i < fiducialSet.getN(); i++) {
//            Matrix y = fiducialSet.getTargetDataset().getPoint(i).getMatrix();
//            Matrix z = fiducialSet.getSourceDataset().getPoint(i).getMatrix();
//            Matrix tmp = y.minus(R.times(z)).minus(T);
//            sum += tmp.transpose().times(getSigmaInv(point)).times(tmp).get(0,0);
//        }
//
//        return sum * 1/2;
//    }

    @Override
    public double value(double[] point) {
        double value = getDerivativeStructure(point).getValue();
        System.out.println(Arrays.toString(point));
        System.out.println(value);
        return value;
    }

    abstract protected Matrix getSigmaInv(double[] point);
    abstract protected void checkParameters(double[] point);
    abstract protected DerivativeStructure getDerivativeStructure(double[] point);

//    private Matrix getSigmaInv() {
//        return sigmaInv;
//    }

    private Matrix getR(double theta) {
        return new Matrix(new double[][] {
            { cos(theta), -sin(theta) },
            { sin(theta), cos(theta) }
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
