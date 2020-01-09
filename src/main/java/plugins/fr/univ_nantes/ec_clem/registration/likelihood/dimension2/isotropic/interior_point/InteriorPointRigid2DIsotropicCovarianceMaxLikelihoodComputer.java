package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic.interior_point;

import org.coinor.Ipopt;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.IpoptSolver;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.OptimizationResult;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DCovarianceMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic.ConstrainedCovarianceOptimProblem;
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;

import javax.inject.Inject;

public class InteriorPointRigid2DIsotropicCovarianceMaxLikelihoodComputer extends Rigid2DCovarianceMaxLikelihoodComputer {

    @Inject
    public InteriorPointRigid2DIsotropicCovarianceMaxLikelihoodComputer(MatrixUtil matrixUtil) {
        super(matrixUtil);
    }

    @Override
    protected OptimizationResult optimize(FiducialSet fiducialSet, Transformation transformation) {
        ConstrainedCovarianceOptimProblem optimProblem = new ConstrainedCovarianceOptimProblem(fiducialSet, transformation);
        Ipopt ipopt = new IpoptSolver(optimProblem);
        ipopt.OptimizeNLP();
        optimProblem.close();
        return new OptimizationResult(
            ipopt.getVariableValues(),
            ipopt.getObjectiveValue()
        );
    }
}
