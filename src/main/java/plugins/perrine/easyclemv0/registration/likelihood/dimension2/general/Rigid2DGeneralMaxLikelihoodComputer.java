package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import Jama.Matrix;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.apache.commons.math3.random.RandomVectorGenerator;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;

import javax.inject.Inject;
import java.util.Arrays;

public class Rigid2DGeneralMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    private MatrixUtil matrixUtil;
    private CovarianceMatrixComputer covarianceMatrixComputer;

    @Inject
    public Rigid2DGeneralMaxLikelihoodComputer() {
        DaggerRigid2DGeneralLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    protected double[] optimize(FiducialSet fiducialSet) {
        Rigid2DGeneralMaxLikelihoodIpopt rigid2DGeneralMaxLikelihoodIpopt = new Rigid2DGeneralMaxLikelihoodIpopt(fiducialSet);
        rigid2DGeneralMaxLikelihoodIpopt.setIntegerOption("max_iter", 20000);
        rigid2DGeneralMaxLikelihoodIpopt.setNumericOption("print_frequency_time", 1);
        rigid2DGeneralMaxLikelihoodIpopt.setIntegerOption("print_frequency_iter", 1);
        rigid2DGeneralMaxLikelihoodIpopt.setStringOption("linear_solver", "mumps");
        rigid2DGeneralMaxLikelihoodIpopt.OptimizeNLP();
        System.out.println(Arrays.toString(rigid2DGeneralMaxLikelihoodIpopt.getVariableValues()));
        System.out.println(Arrays.toString(rigid2DGeneralMaxLikelihoodIpopt.getConstraintValues()));
        return rigid2DGeneralMaxLikelihoodIpopt.getVariableValues();
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
