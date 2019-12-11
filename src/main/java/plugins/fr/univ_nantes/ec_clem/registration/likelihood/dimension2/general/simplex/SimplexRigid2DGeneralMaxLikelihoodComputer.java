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
package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.simplex;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.MultiDirectionalSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.Rigid2DMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.BaseOptimProblem;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class SimplexRigid2DGeneralMaxLikelihoodComputer extends Rigid2DMaxLikelihoodComputer {

    @Inject
    public SimplexRigid2DGeneralMaxLikelihoodComputer(MatrixUtil matrixUtil) {
        super(matrixUtil);
    }

    @Override
    protected double[] optimize(FiducialSet fiducialSet) {
        BaseOptimProblem optimProblem = new BaseOptimProblem(fiducialSet);
        PointValuePair optimize = optimize(optimProblem);
        optimProblem.close();
        return optimize.getPoint();
    }

    private PointValuePair optimize(BaseOptimProblem optimProblem) {
        return new MultiStartMultivariateOptimizer(
            new SimplexOptimizer(1e-20, 1e-20),
            1,
            () -> optimProblem.getStartingPoint()
        ).optimize(
            GoalType.MINIMIZE,
            new ObjectiveFunction(
                point -> optimProblem.getObjectiveValue(point)
            ),
            new MultiDirectionalSimplex(optimProblem.getNParameters()),
            new InitialGuess(optimProblem.getStartingPoint()),
            MaxEval.unlimited()
        );
    }
}
