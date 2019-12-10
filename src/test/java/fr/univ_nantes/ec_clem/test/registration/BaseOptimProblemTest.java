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
package fr.univ_nantes.ec_clem.test.registration;

import plugins.fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.BaseOptimProblem;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.testng.Assert.assertEquals;

public class BaseOptimProblemTest {

    private TestFiducialSetFactory testFiducialSetFactory;

    public BaseOptimProblemTest() {
        DaggerBaseOptimProblemTestComponent.create().inject(this);
    }

    @Test
    void testObjectiveValue() {
        FiducialSet identityFiducialSet = testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100();
        BaseOptimProblem subjectUnderTest = new BaseOptimProblem(identityFiducialSet);
        double[] point = new double[]{ 0, 0, 0, 0.1d, 0, 0, 0.1d };
        assertEquals(48.88197, subjectUnderTest.getObjectiveValue(point), 0.0001);
    }

    @Test
    void testObjectiveGradient() throws ExecutionException, InterruptedException {
        BaseOptimProblem baseOptimProblem = new BaseOptimProblem(testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100());
        double[] point = baseOptimProblem.getStartingPoint();

        double[] epsilonArray = new double[baseOptimProblem.getNParameters()];
        double epsilon = 0.00001;

        double[] plusEpsilon = new double[baseOptimProblem.getNParameters()];
        double[] minusEpsilon = new double[baseOptimProblem.getNParameters()];

        double[] derivativeArray = baseOptimProblem.getObjectiveGradient(point);
        for(int i = 0; i < baseOptimProblem.getNParameters(); i++) {
            epsilonArray[i] += epsilon;
            Arrays.setAll(plusEpsilon, j -> point[j] + epsilonArray[j]);
            Arrays.setAll(minusEpsilon, j -> point[j] - epsilonArray[j]);
            double expected = (baseOptimProblem.getObjectiveValue(plusEpsilon) - baseOptimProblem.getObjectiveValue(minusEpsilon)) / (2d * epsilon);
            assertEquals(expected, derivativeArray[i], 1e-2);
            epsilonArray[i] = 0;
        }
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }
}
