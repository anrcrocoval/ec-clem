package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.conjugate_gradient;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component
public interface ConjugateGradientRigid2DGeneralLikelihoodComputerComponent {
    void inject(ConjugateGradientRigid2DGeneralMaxLikelihoodComputer rigid2DGeneralMaxLikelihoodComputer2);
}