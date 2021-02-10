package plugins.perrine.easyclemv0.ec_clem.sequence;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface SequenceMergerComponent {
    void inject(SequenceMerger sequenceMerger);
}