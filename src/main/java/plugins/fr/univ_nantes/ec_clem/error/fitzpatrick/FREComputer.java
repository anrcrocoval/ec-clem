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
package plugins.fr.univ_nantes.ec_clem.error.fitzpatrick;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;

import javax.inject.Inject;

public class FREComputer {

    private Mean mean = new Mean();

    @Inject
    public FREComputer() {
    }

    public double getExpectedSquareFRE(Dataset transformedSourceDataset, Dataset targetDataset) {
        mean.clear();
        for(int i = 0; i < transformedSourceDataset.getN(); i++) {
            mean.increment(
                transformedSourceDataset.getPoint(i).minus(targetDataset.getPoint(i)).getSumOfSquare()
            );
        }
        return mean.getResult();
    }
}
