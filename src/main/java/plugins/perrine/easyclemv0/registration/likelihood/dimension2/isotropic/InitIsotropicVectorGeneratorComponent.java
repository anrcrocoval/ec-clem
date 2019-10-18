package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component
public interface InitIsotropicVectorGeneratorComponent {
    void inject(InitIsotropicVectorGenerator initIsotropicVectorGenerator);
}