package plugins.perrine.easyclemv0.transformation_schema_loader;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemTransformationSchemaLoaderComponent {
    void inject(EcClemTransformationSchemaLoader ecClemTransformationSchemaLoader);
}