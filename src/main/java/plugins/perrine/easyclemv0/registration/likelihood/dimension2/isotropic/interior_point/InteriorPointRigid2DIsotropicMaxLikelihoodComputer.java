package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic.interior_point;

import org.coinor.Ipopt;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.IpoptSolver;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic.ConstrainedOptimProblem;
import javax.inject.Inject;

public class InteriorPointRigid2DIsotropicMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    @Inject
    public InteriorPointRigid2DIsotropicMaxLikelihoodComputer() {
        super();
        DaggerInteriorPointRigid2DIsotropicLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    protected double[] optimize(FiducialSet fiducialSet) {
        Ipopt ipopt = new IpoptSolver(new ConstrainedOptimProblem(fiducialSet));
        ipopt.OptimizeNLP();
        return ipopt.getVariableValues();
    }
}
