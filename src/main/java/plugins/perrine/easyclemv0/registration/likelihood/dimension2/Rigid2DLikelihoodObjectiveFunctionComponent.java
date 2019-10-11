package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component
public interface Rigid2DLikelihoodObjectiveFunctionComponent {
    void inject(Rigid2DMaxLikelihoodObjectiveFunction rigid2DMaxLikelihoodObjectiveFunction);
}
