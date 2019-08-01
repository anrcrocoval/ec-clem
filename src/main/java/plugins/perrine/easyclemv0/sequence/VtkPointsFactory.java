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
package plugins.perrine.easyclemv0.sequence;

import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import vtk.vtkPoints;

import javax.inject.Inject;

public class VtkPointsFactory {

    @Inject
    public VtkPointsFactory() {}

    public vtkPoints getFrom(Dataset dataset) {
        vtkPoints points = new vtkPoints();
        points.SetNumberOfPoints(dataset.getN());
        for (int i = 0; i < dataset.getN(); i++) {
            points.SetPoint(i, dataset.getPoint(i).getMatrix().transpose().getArray()[0]);
        }
        return points;
    }
}
