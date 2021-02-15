package plugins.perrine.ec_clem.error;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemErrorComponent {
    void inject(EcClemError ecClemError);
}