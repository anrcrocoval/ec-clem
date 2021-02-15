package plugins.perrine.ec_clem.transform;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemTransformComponent {
    void inject(EcClemTransform ecClemTransform);
}