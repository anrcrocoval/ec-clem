package test.plugins.perrine.easyclemv0.registration;

import dagger.Component;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient;

@Component
public interface DerivativeTestComponent {
    void inject(DerivativeTest derivativeTest);
}