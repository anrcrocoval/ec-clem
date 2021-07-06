package plugins.perrine.ec_clem.autofinder;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemAutoFinderComponent {
    void inject(EcClemAutoFinder ecClemAutoFinder);
}