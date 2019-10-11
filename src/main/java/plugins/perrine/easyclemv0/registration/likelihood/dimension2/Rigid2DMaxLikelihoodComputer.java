package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import Jama.Matrix;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;
import org.apache.commons.math3.random.UniformRandomGenerator;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.registration.TransformationComputer;
import plugins.perrine.easyclemv0.transformation.Similarity;
import javax.inject.Inject;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES;

public class Rigid2DMaxLikelihoodComputer implements TransformationComputer {

    private CovarianceMatrixComputer covarianceMatrixComputer;
    private MatrixUtil matrixUtil;

    @Inject
    public Rigid2DMaxLikelihoodComputer(CovarianceMatrixComputer covarianceMatrixComputer, MatrixUtil matrixUtil) {
        this.covarianceMatrixComputer = covarianceMatrixComputer;
        this.matrixUtil = matrixUtil;
    }

    @Override
    public Similarity compute(FiducialSet fiducialSet) {
        NonLinearConjugateGradientOptimizer nonLinearConjugateGradientOptimizer = new NonLinearConjugateGradientOptimizer(
            FLETCHER_REEVES,
            new SimpleValueChecker(0.0000000001, 0.0000000001)
        );
        MultiStartMultivariateOptimizer multiStartMultivariateOptimizer = new MultiStartMultivariateOptimizer(
            nonLinearConjugateGradientOptimizer,
            100,
            new UncorrelatedRandomVectorGenerator(3, new UniformRandomGenerator(new SynchronizedRandomGenerator(new JDKRandomGenerator())))
        );
        PointValuePair optimize = multiStartMultivariateOptimizer.optimize(
            GoalType.MINIMIZE,
            new ObjectiveFunction(new Rigid2DMaxLikelihoodObjectiveFunction(fiducialSet)),
            new ObjectiveFunctionGradient(new Rigid2DMaxLikelihoodObjectiveFunctionGradient(fiducialSet)),
            new InitialGuess(new double[]{ 0, 0, 0 }),
            MaxEval.unlimited(),
            MaxIter.unlimited()
        );
        return new Similarity(
            new Matrix(new double [][] {
                { cos(optimize.getPoint()[2]), -sin(optimize.getPoint()[2]) },
                { sin(optimize.getPoint()[2]), cos(optimize.getPoint()[2]) }
            }),
            new Matrix(new double[][] {
                {  optimize.getPoint()[0] },
                { optimize.getPoint()[1] }
            }),
            Matrix.identity(2,2)
        );
    }
}
