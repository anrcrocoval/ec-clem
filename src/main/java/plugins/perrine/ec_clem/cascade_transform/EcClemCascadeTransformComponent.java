package plugins.perrine.ec_clem.cascade_transform;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemCascadeTransformComponent {
    void inject(EcClemCascadeTransform ecClemCascadeTransform);
}