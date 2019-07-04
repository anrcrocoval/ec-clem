package plugins.perrine.easyclemv0.factory.vtk;

import Jama.Matrix;
import plugins.perrine.easyclemv0.model.transformation.AffineTransformation;
import plugins.perrine.easyclemv0.model.transformation.SplineTransformation;
import plugins.perrine.easyclemv0.model.transformation.Transformation;
import vtk.vtkAbstractTransform;
import vtk.vtkMatrix4x4;
import vtk.vtkTransform;

public class VtkAbstractTransformFactory {

    public vtkAbstractTransform getFrom(Transformation transformation) {
        if(transformation instanceof AffineTransformation) {
            Matrix transformationMatrix = ((AffineTransformation) transformation).getMatrix();
            if (transformationMatrix.getRowDimension() != 4) {
                throw new RuntimeException("Use this class for 3D transformation only");
            }
            vtkTransform vtkTransform = new vtkTransform();
            vtkMatrix4x4 vtkMatrix = new vtkMatrix4x4();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    vtkMatrix.SetElement(i, j, transformationMatrix.get(i, j));
                }
            }
            vtkTransform.SetMatrix(vtkMatrix);
            return vtkTransform;
        }

        if(transformation instanceof SplineTransformation) {
            return ((SplineTransformation) transformation).getSplineTransform();
        }

        throw new RuntimeException("Unsupported transformation");
    }
}
