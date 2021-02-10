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
package plugins.perrine.easyclemv0.ec_clem.transformation;

import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;

public interface Transformation {
    Dataset apply(Dataset dataset);
    Point apply(Point point);
}
