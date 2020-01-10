package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.interior_point;

import org.coinor.Ipopt;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.IpoptSolver;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.OptimizationResult;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.BaseOptimProblem;

import javax.inject.Inject;

public class InteriorPointRigid2DGeneralMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    @Inject
    public InteriorPointRigid2DGeneralMaxLikelihoodComputer(MatrixUtil matrixUtil) {
        super(matrixUtil);
    }

    @Override
    protected OptimizationResult optimize(FiducialSet fiducialSet) {
        BaseOptimProblem optimProblem = new BaseOptimProblem(fiducialSet);
        Ipopt ipopt = new IpoptSolver(optimProblem);
        ipopt.OptimizeNLP();
        optimProblem.close();
        return new OptimizationResult(
            ipopt.getVariableValues(),
            ipopt.getObjectiveValue()
        );
    }
}
