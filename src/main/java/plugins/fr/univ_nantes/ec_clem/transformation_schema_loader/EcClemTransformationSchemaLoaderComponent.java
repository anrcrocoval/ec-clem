package plugins.fr.univ_nantes.ec_clem.transformation_schema_loader;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemTransformationSchemaLoaderComponent {
    void inject(EcClemTransformationSchemaLoader ecClemTransformationSchemaLoader);
}