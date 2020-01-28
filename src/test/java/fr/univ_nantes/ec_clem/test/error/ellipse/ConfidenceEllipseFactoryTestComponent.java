package fr.univ_nantes.ec_clem.test.error.ellipse;

import dagger.Component;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputerModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
    Rigid2DMaxLikelihoodComputerModule.class
})
public interface ConfidenceEllipseFactoryTestComponent {
    void inject(ConfidenceEllipseFactoryTest confidenceEllipseFactoryTest);
}