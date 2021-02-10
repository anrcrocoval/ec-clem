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
package plugins.fr.univ_nantes.ec_clem.ec_clem.sequence;

import icy.vtk.VtkUtil;
import vtk.vtkImageGridSource;
import javax.inject.Inject;

public class VtkImageGridSourceFactory {

    @Inject
    public VtkImageGridSourceFactory() {}

    public vtkImageGridSource getFrom(int xSize, int ySize, int zSize, double spacingX, double spacingY, double spacingZ) {
        vtkImageGridSource sourceGrid = new vtkImageGridSource();
        sourceGrid.SetDataExtent(0, xSize -1, 0,  ySize - 1, 0,  zSize - 1);
        sourceGrid.SetLineValue(255);
        sourceGrid.SetFillValue(0);
        sourceGrid.SetDataScalarType(VtkUtil.VTK_UNSIGNED_CHAR);
        sourceGrid.SetDataSpacing(spacingX, spacingY, spacingZ);
        sourceGrid.SetGridSpacing(Math.round((float) xSize / 10), Math.round((float) ySize / 10), 0);
        sourceGrid.ReleaseDataFlagOn();
        sourceGrid.Update();
        return sourceGrid;
    }
}
