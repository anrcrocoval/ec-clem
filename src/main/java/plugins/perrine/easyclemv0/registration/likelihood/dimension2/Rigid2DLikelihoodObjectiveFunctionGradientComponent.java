package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component
public interface Rigid2DLikelihoodObjectiveFunctionGradientComponent {
    void inject(Rigid2DMaxLikelihoodObjectiveFunctionGradient rigid2DMaxLikelihoodObjectiveFunctionGradient);
}
