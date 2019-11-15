package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.interior_point;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component
public interface InteriorPointRigid2DGeneralLikelihoodComputerComponent {
    void inject(InteriorPointRigid2DGeneralMaxLikelihoodComputer rigid2DGeneralMaxLikelihoodComputer);
}