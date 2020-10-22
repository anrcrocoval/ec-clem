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

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.SplineTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Transformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.matrix.MatrixUtil;
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
               Matrix correctedmatrixinverse=new Matrix(4, 4); 
               for(int i=0;i<2;i++)
            	   for(int j=0;j<2;j++)
            		   correctedmatrixinverse.set(i, j, matrixInverse.get(i, j));
               correctedmatrixinverse.set(0, 3,matrixInverse.get(0, 2));
               correctedmatrixinverse.set(1, 3,matrixInverse.get(1, 2));
               correctedmatrixinverse.set(3, 3,1);
               
               matrixInverse=correctedmatrixinverse.copy();
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
