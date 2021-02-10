package plugins.perrine.easyclemv0.transform;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemTransformComponent {
    void inject(EcClemTransform ecClemTransform);
}