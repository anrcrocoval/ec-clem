package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import dagger.Component;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient;

import javax.inject.Singleton;

@Singleton
@Component
public interface Rigid2DIsotropicLikelihoodObjectiveFunctionGradientComponent {
    void inject(Rigid2DIsotropicMaxLikelihoodObjectiveFunctionGradient rigid2DIsotropicMaxLikelihoodObjectiveFunctionGradient);
}
