package plugins.fr.univ_nantes.ec_clem;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemTransformComponent {
    void inject(EcClemTransform ecClemTransform);
}