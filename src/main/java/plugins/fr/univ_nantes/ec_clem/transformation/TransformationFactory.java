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
package plugins.fr.univ_nantes.ec_clem.transformation;

import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import plugins.fr.univ_nantes.ec_clem.registration.AffineTransformationComputer;
import plugins.fr.univ_nantes.ec_clem.registration.NonRigidTransformationComputer;
import plugins.fr.univ_nantes.ec_clem.registration.RigidTransformationComputer;
import plugins.fr.univ_nantes.ec_clem.registration.SimilarityTransformationComputer;

import javax.inject.Inject;

public class TransformationFactory {

    private RigidTransformationComputer rigidTransformationComputer;
    private SimilarityTransformationComputer similarityTransformationComputer;
    private NonRigidTransformationComputer nonRigidTransformationComputer;
    private AffineTransformationComputer affineTransformationComputer;

    @Inject
    public TransformationFactory(RigidTransformationComputer rigidTransformationComputer, SimilarityTransformationComputer similarityTransformationComputer, NonRigidTransformationComputer nonRigidTransformationComputer, AffineTransformationComputer affineTransformationComputer) {
        this.rigidTransformationComputer = rigidTransformationComputer;
        this.similarityTransformationComputer = similarityTransformationComputer;
        this.nonRigidTransformationComputer = nonRigidTransformationComputer;
        this.affineTransformationComputer = affineTransformationComputer;
    }

    public Transformation getFrom(TransformationSchema transformationSchema) {
        switch (transformationSchema.getTransformationType()) {
            case RIGID: return rigidTransformationComputer.compute(transformationSchema.getFiducialSet());
            case SIMILARITY: return similarityTransformationComputer.compute(transformationSchema.getFiducialSet());
            case AFFINE: return affineTransformationComputer.compute(transformationSchema.getFiducialSet());
            case SPLINE: return  nonRigidTransformationComputer.compute(transformationSchema.getFiducialSet());
            default : throw new RuntimeException("Case not implemented");
        }
    }
}
