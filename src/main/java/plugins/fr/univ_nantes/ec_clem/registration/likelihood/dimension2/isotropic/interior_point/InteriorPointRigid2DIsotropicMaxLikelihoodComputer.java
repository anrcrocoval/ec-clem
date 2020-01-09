package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic.interior_point;

import org.coinor.Ipopt;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.IpoptSolver;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.OptimizationResult;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic.ConstrainedOptimProblem;

import javax.inject.Inject;

public class InteriorPointRigid2DIsotropicMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    @Inject
    public InteriorPointRigid2DIsotropicMaxLikelihoodComputer(MatrixUtil matrixUtil) {
        super(matrixUtil);
    }

    @Override
    protected OptimizationResult optimize(FiducialSet fiducialSet) {
        ConstrainedOptimProblem optimProblem = new ConstrainedOptimProblem(fiducialSet);
        Ipopt ipopt = new IpoptSolver(optimProblem);
        ipopt.OptimizeNLP();
        optimProblem.close();
        return new OptimizationResult(
            ipopt.getVariableValues(),
            ipopt.getObjectiveValue()
        );
    }
}
