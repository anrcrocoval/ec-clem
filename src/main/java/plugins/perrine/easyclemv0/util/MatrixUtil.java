package plugins.perrine.easyclemv0.util;

import Jama.Matrix;
import vtk.vtkMatrix4x4;

public abstract class MatrixUtil {

    public static Matrix convert(vtkMatrix4x4 M) {
        Matrix result = new Matrix(4, 4);
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                result.set(i, j, M.GetElement(i, j));
            }
        }
        return result;
    }
}
