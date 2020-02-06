package fr.univ_nantes.ec_clem.test.error.ellipse;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface ConfidenceEllipseFactoryTestComponent {
    void inject(ConfidenceEllipseFactoryTest confidenceEllipseFactoryTest);
}