package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2;

import dagger.Module;
import dagger.Provides;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.conjugate_gradient.ConjugateGradientRigid2DGeneralMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.interior_point.InteriorPointRigid2DGeneralMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic.interior_point.InteriorPointRigid2DIsotropicMaxLikelihoodComputer;

import javax.inject.Named;

@Module
public class Rigid2DMaxLikelihoodComputerModule {

    @Provides
    @Named("ipopt_general")
    public Rigid2DMaxLikelihoodComputer provideInteriorPointRigid2DGeneralMaxLikelihoodComputer(InteriorPointRigid2DGeneralMaxLikelihoodComputer solver) {
        return solver;
    }

    @Provides
    @Named("ipopt_constrained")
    public Rigid2DMaxLikelihoodComputer provideInteriorPointRigid2DIsotropicMaxLikelihoodComputer(InteriorPointRigid2DIsotropicMaxLikelihoodComputer solver) {
        return solver;
    }

    @Provides
    @Named("conjugate_gradient")
    public Rigid2DMaxLikelihoodComputer provideConjugateGradientRigid2DGeneralMaxLikelihoodComputer(ConjugateGradientRigid2DGeneralMaxLikelihoodComputer solver) {
        return solver;
    }
}
