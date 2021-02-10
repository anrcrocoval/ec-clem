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
package plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Similarity;

public class DatasetTransformer {

    public Dataset apply(Dataset dataset, Matrix transformation) {
        Matrix M = dataset.getMatrix().times(transformation);
        return new Dataset(M.getArray(), dataset.getPointType());
    }

    public Dataset apply(Dataset dataset, Similarity similarity) {
        return similarity.apply(dataset);
    }

    public Dataset apply(Dataset dataset, AffineTransformation affineTransformation) {
        return affineTransformation.apply(dataset);
    }
}
