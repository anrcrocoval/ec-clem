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
package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.conjugate_gradient;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.BaseOptimProblem;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ConjugateGradientRigid2DGeneralMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    @Inject
    public ConjugateGradientRigid2DGeneralMaxLikelihoodComputer() {
        super();
        DaggerConjugateGradientRigid2DGeneralLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    protected double[] optimize(FiducialSet fiducialSet) {
        BaseOptimProblem optimProblem = new BaseOptimProblem(fiducialSet);
        NonLinearConjugateGradientOptimizer solver = new NonLinearConjugateGradientOptimizer(
            NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
            new SimpleValueChecker(1e-16, 1e-16, 500)
        );
        MultiStartMultivariateOptimizer multiStartMultivariateOptimizer = new MultiStartMultivariateOptimizer(solver, 10, new RandomVectorGenerator() {
            private Random random = new Random();

            @Override
            public double[] nextVector() {
                return new double[]{
                    random.nextDouble(),
                    random.nextDouble(),
                    random.nextDouble(),
                    random.nextDouble(),
                    random.nextDouble(),
                    random.nextDouble(),
                    random.nextDouble()
                };
            }
        });
        PointValuePair optimize = multiStartMultivariateOptimizer.optimize(
            GoalType.MINIMIZE,
            new ObjectiveFunction(
                point -> optimProblem.getObjectiveValue(point)
            ),
            new ObjectiveFunctionGradient(point -> {
                double[] objectiveGradient = null;
                try {
                    objectiveGradient = optimProblem.getObjectiveGradient(point);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return objectiveGradient;
            }),
            new InitialGuess(optimProblem.getStartingPoint()),
            MaxEval.unlimited()
        );
//        System.out.println(Arrays.toString(optimize.getPoint()));
        return optimize.getPoint();
    }
}
