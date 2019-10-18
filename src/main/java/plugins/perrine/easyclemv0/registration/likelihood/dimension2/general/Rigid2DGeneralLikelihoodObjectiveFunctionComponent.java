package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component
public interface Rigid2DGeneralLikelihoodObjectiveFunctionComponent {
    void inject(Rigid2DGeneralMaxLikelihoodObjectiveFunction rigid2DGeneralMaxLikelihoodObjectiveFunction);
}