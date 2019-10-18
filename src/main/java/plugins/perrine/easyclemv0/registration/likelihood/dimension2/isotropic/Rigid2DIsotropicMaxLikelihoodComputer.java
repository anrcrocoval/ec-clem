package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import Jama.Matrix;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.apache.commons.math3.random.RandomVectorGenerator;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodObjectiveFunction;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.DaggerRigid2DGeneralLikelihoodComputerComponent;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.InitGeneralVectorGenerator;

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
    protected Rigid2DMaxLikelihoodObjectiveFunction getObjectiveFunction(FiducialSet fiducialSet) {
        return new Rigid2DIsotropicMaxLikelihoodObjectiveFunction(fiducialSet);
    }

    @Override
    protected MultivariateVectorFunction getObjectiveFunctionGradient(FiducialSet fiducialSet, MultivariateFunctionMappingAdapter adapter) {
        return new Rigid2DIsotropicMaxLikelihoodObjectiveFunctionGradient(fiducialSet, adapter);
    }

    @Override
    protected RandomVectorGenerator getRandomVectorGenerator(FiducialSet fiducialSet) {
        return new InitIsotropicVectorGenerator(fiducialSet);
    }

    @Override
    protected double[] getInitialGuess(FiducialSet fiducialSet) {
        Matrix sigmaInv = matrixUtil.pseudoInverse(covarianceMatrixComputer.compute(fiducialSet.getTargetDataset().getMatrix()));
        return new double[]{0, 0, 0, 0};
    }

    @Override
    protected int getNParameters() {
        return 4;
    }

    @Override
    protected double[] getParametersLowerBounds() {
        return new double[] {
            -10000,
            -10000,
            0,
            0.00000001
        };
    }

    @Override
    protected double[] getParametersUpperBounds() {
        return new double[] {
            10000,
            10000,
            2 * Math.PI,
            1000
        };
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
