package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

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

public class Rigid2DIsotropicMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    private MatrixUtil matrixUtil;
    private CovarianceMatrixComputer covarianceMatrixComputer;

    @Inject
    public Rigid2DIsotropicMaxLikelihoodComputer() {
        DaggerRigid2DIsotropicLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    protected double[] optimize(FiducialSet fiducialSet) {
        Rigid2DIsotropicMaxLikelihoodIpopt rigid2DGeneralMaxLikelihoodIpopt = new Rigid2DIsotropicMaxLikelihoodIpopt(fiducialSet);
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
