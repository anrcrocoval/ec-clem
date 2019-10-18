package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import dagger.Component;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.Rigid2DGeneralMaxLikelihoodComputer;

import javax.inject.Singleton;

@Singleton
@Component
public interface Rigid2DIsotropicLikelihoodComputerComponent {
    void inject(Rigid2DIsotropicMaxLikelihoodComputer rigid2DIsotropicMaxLikelihoodComputer);
}