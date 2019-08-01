package plugins.perrine.easyclemv0;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EasyCLEMv0Component {
    void inject(EasyCLEMv0 easyCLEMv0);
}
