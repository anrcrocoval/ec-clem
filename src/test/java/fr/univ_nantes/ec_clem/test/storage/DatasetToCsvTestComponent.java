package fr.univ_nantes.ec_clem.test.storage;

import dagger.Component;

@Component()
public interface DatasetToCsvTestComponent {
    void inject(DatasetToCsvTest datasetToCsvTest);
}