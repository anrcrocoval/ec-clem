/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package fr.univ_nantes.ec_clem.test;

import dagger.Component;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DCovarianceMaxLikelihoodComputerModule;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputerModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
    Rigid2DMaxLikelihoodComputerModule.class,
    Rigid2DCovarianceMaxLikelihoodComputerModule.class
})
public interface IgnoredTestComponent {
    void inject(IgnoredTest ignoredTest);
}
