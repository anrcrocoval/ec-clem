package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import org.coinor.Ipopt;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.IpoptSolver;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import javax.inject.Inject;

public class Rigid2DIsotropicMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    @Inject
    public Rigid2DIsotropicMaxLikelihoodComputer() {
        super();
        DaggerRigid2DIsotropicLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    protected double[] optimize(FiducialSet fiducialSet) {
        Ipopt ipopt = new IpoptSolver(new ConstrainedOptimProblem(fiducialSet));
        ipopt.OptimizeNLP();
        return ipopt.getVariableValues();
    }
}
