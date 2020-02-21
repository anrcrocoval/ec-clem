package plugins.fr.univ_nantes.ec_clem;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemErrorComponent {
    void inject(EcClemError ecClemError);
}