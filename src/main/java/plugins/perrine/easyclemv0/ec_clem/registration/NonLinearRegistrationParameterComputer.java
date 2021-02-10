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
package plugins.perrine.easyclemv0.ec_clem.registration;

import Jama.Matrix;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.ec_clem.sequence.VtkPointsFactory;
import plugins.perrine.easyclemv0.ec_clem.transformation.DaggerSplineTransformationComponent;
import plugins.perrine.easyclemv0.ec_clem.transformation.SplineTransformationComponent;
import vtk.vtkThinPlateSplineTransform;
import javax.inject.Inject;

public class NonLinearRegistrationParameterComputer implements RegistrationParameterComputer {

    private SplineTransformationComponent splineTransformationComponent;
    private VtkPointsFactory vtkPointsFactory;

    @Inject
    public NonLinearRegistrationParameterComputer(VtkPointsFactory vtkPointsFactory) {
        this.vtkPointsFactory = vtkPointsFactory;
        splineTransformationComponent = DaggerSplineTransformationComponent.create();
    }

    public RegistrationParameter compute(FiducialSet fiducialSet) {
        return compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }

    private RegistrationParameter compute(Dataset sourceDataset, Dataset targetDataset) {
        vtkThinPlateSplineTransform vtkSplineTransformation = new vtkThinPlateSplineTransform();
        vtkSplineTransformation.SetSourceLandmarks(vtkPointsFactory.getFrom(sourceDataset));
        vtkSplineTransformation.SetTargetLandmarks(vtkPointsFactory.getFrom(targetDataset));
        vtkSplineTransformation.SetBasisToR2LogR();
        return new RegistrationParameter(
            splineTransformationComponent.getSplineTransformation().setSplineTransform(vtkSplineTransformation),
            Matrix.identity(sourceDataset.getDimension(), targetDataset.getDimension()),
            Double.NaN
        );
    }
}
