package fr.univ_nantes.ec_clem.test.storage;

import dagger.Component;

@Component()
public interface TransformationToCsvTestComponent {
    void inject(TransformationToCsvTest transformationToCsvTest);
}