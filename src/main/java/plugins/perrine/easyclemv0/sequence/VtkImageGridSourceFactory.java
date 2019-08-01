package plugins.perrine.easyclemv0.sequence;

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
        sourceGrid.SetDataScalarType(icy.vtk.VtkUtil.VTK_UNSIGNED_CHAR);
        sourceGrid.SetDataSpacing(spacingX, spacingY, spacingZ);
        sourceGrid.SetGridSpacing(Math.round(xSize / 10), Math.round(ySize / 10), 0);
        sourceGrid.Update();
        return sourceGrid;
    }
}
