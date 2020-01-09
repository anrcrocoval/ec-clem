package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2;

import dagger.Module;
import dagger.Provides;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic.interior_point.InteriorPointRigid2DIsotropicCovarianceMaxLikelihoodComputer;

@Module
public class Rigid2DCovarianceMaxLikelihoodComputerModule {

    @Provides
    public Rigid2DCovarianceMaxLikelihoodComputer provideInteriorPointRigid2DIsotropicMaxLikelihoodComputer(InteriorPointRigid2DIsotropicCovarianceMaxLikelihoodComputer solver) {
        return solver;
    }
}
