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
package plugins.fr.univ_nantes.ec_clem.ec_clem.transformation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import plugins.fr.univ_nantes.ec_clem.ec_clem.registration.*;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import plugins.fr.univ_nantes.ec_clem.ec_clem.registration.AffineRegistrationParameterComputer;
import plugins.fr.univ_nantes.ec_clem.ec_clem.registration.SimilarityRegistrationParameterComputer;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegistrationParameterFactory {

    private RigidRegistrationParameterComputer rigidTransformationComputer;
    private SimilarityRegistrationParameterComputer similarityTransformationComputer;
    private NonLinearRegistrationParameterComputer nonLinearTransformationComputer;
    private AffineRegistrationParameterComputer affineTransformationComputer;

    private LoadingCache<TransformationSchema, RegistrationParameter> cache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .weakKeys()
        .build(new CacheLoader<TransformationSchema, RegistrationParameter>() {
            public RegistrationParameter load(TransformationSchema key) {
                return get(key);
            }
        });

    @Inject
    public RegistrationParameterFactory(
        RigidRegistrationParameterComputer rigidTransformationComputer,
        SimilarityRegistrationParameterComputer similarityTransformationComputer,
        NonLinearRegistrationParameterComputer nonLinearTransformationComputer,
        AffineRegistrationParameterComputer affineTransformationComputer
    ) {
        this.rigidTransformationComputer = rigidTransformationComputer;
        this.similarityTransformationComputer = similarityTransformationComputer;
        this.nonLinearTransformationComputer = nonLinearTransformationComputer;
        this.affineTransformationComputer = affineTransformationComputer;
    }

    public RegistrationParameter getFrom(TransformationSchema transformationSchema) {
        return cache.getUnchecked(transformationSchema);
    }

    private RegistrationParameter get(TransformationSchema transformationSchema) {
        switch (transformationSchema.getTransformationType()) {
            case RIGID: return rigidTransformationComputer.compute(transformationSchema.getFiducialSet());
            case SIMILARITY: return similarityTransformationComputer.compute(transformationSchema.getFiducialSet());
            case AFFINE: return affineTransformationComputer.compute(transformationSchema.getFiducialSet());
            case SPLINE: return  nonLinearTransformationComputer.compute(transformationSchema.getFiducialSet());
            default : throw new RuntimeException("Case not implemented");
        }
    }
}
