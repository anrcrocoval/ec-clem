package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic.interior_point;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component
public interface InteriorPointRigid2DIsotropicLikelihoodComputerComponent {
    void inject(InteriorPointRigid2DIsotropicMaxLikelihoodComputer rigid2DIsotropicMaxLikelihoodComputer);
}