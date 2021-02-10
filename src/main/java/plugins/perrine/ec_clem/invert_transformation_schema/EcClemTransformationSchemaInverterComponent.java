package plugins.fr.univ_nantes.ec_clem.invert_transformation_schema;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component()
public interface EcClemTransformationSchemaInverterComponent {
    void inject(EcClemTransformationSchemaInverter ecClemTransformationSchemaInverter);
}