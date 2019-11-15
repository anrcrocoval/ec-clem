package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.interior_point;

import org.coinor.Ipopt;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.IpoptSolver;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.BaseOptimProblem;

import javax.inject.Inject;
import java.util.Arrays;

public class InteriorPointRigid2DGeneralMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    @Inject
    public InteriorPointRigid2DGeneralMaxLikelihoodComputer() {
        super();
        DaggerInteriorPointRigid2DGeneralLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    protected double[] optimize(FiducialSet fiducialSet) {
        Ipopt ipopt = new IpoptSolver(new BaseOptimProblem(fiducialSet));
        ipopt.OptimizeNLP();
        System.out.println(Arrays.toString(ipopt.getVariableValues()));
        return ipopt.getVariableValues();
    }
}
