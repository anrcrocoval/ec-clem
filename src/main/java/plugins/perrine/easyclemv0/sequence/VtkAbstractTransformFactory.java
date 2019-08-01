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

import Jama.Matrix;
import plugins.perrine.easyclemv0.transformation.AffineTransformation;
import plugins.perrine.easyclemv0.transformation.SplineTransformation;
import plugins.perrine.easyclemv0.transformation.Transformation;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import vtk.vtkAbstractTransform;
import vtk.vtkMatrix4x4;
import vtk.vtkTransform;

import javax.inject.Inject;

public class VtkAbstractTransformFactory {

    private MatrixUtil matrixUtil;

    @Inject
    public VtkAbstractTransformFactory(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    public vtkAbstractTransform getFrom(Transformation transformation) {
        if(transformation instanceof AffineTransformation) {
            Matrix matrixInverse = matrixUtil.pseudoInverse(((AffineTransformation) transformation).getHomogeneousMatrix());
            if (matrixInverse.getRowDimension() != 4) {
                throw new RuntimeException("Use this class for 3D transformation only");
            }
            vtkTransform vtkTransform = new vtkTransform();
            vtkMatrix4x4 vtkMatrix = new vtkMatrix4x4();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    vtkMatrix.SetElement(i, j, matrixInverse.get(i, j));
                }
            }
            vtkTransform.SetMatrix(vtkMatrix);
            return vtkTransform;
        }

        if(transformation instanceof SplineTransformation) {
            return ((SplineTransformation) transformation).getSplineTransform().GetInverse();
        }

        throw new RuntimeException("Unsupported transformation");
    }
}
