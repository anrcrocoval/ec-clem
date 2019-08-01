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
package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.sequence.VtkPointsFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.transformation.DaggerSplineTransformationComponent;
import plugins.perrine.easyclemv0.transformation.SplineTransformation;
import plugins.perrine.easyclemv0.transformation.SplineTransformationComponent;
import vtk.vtkThinPlateSplineTransform;

import javax.inject.Inject;

public class NonRigidTransformationComputer implements TransformationComputer {

    private SplineTransformationComponent splineTransformationComponent;
    private VtkPointsFactory vtkPointsFactory;

    @Inject
    public NonRigidTransformationComputer(VtkPointsFactory vtkPointsFactory) {
        this.vtkPointsFactory = vtkPointsFactory;
        splineTransformationComponent = DaggerSplineTransformationComponent.create();
    }

    public SplineTransformation compute(FiducialSet fiducialSet) {
        return compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }

    private SplineTransformation compute(Dataset sourceDataset, Dataset targetDataset) {
        vtkThinPlateSplineTransform vtkSplineTransformation = new vtkThinPlateSplineTransform();
        vtkSplineTransformation.SetSourceLandmarks(vtkPointsFactory.getFrom(sourceDataset));
        vtkSplineTransformation.SetTargetLandmarks(vtkPointsFactory.getFrom(targetDataset));
        vtkSplineTransformation.SetBasisToR2LogR();
        return splineTransformationComponent.getSplineTransformation().setSplineTransform(vtkSplineTransformation);
    }
}
