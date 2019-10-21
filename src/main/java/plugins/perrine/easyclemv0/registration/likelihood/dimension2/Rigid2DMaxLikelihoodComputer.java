package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import Jama.Matrix;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.TransformationComputer;
import plugins.perrine.easyclemv0.transformation.Similarity;
import java.util.Arrays;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public abstract class Rigid2DMaxLikelihoodComputer implements TransformationComputer {

    @Override
    public Similarity compute(FiducialSet fiducialSet) {
        PointValuePair optimize = optimize(fiducialSet);
        System.out.println(Arrays.toString(optimize.getPoint()));
        System.out.println(optimize.getValue());
        Similarity s = new Similarity(
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
        return s;
    }

    private PointValuePair optimize(FiducialSet fiducialSet) {
        MultivariateFunctionMappingAdapter adapter = new MultivariateFunctionMappingAdapter(
            getObjectiveFunction(fiducialSet),
            getParametersLowerBounds(),
            getParametersUpperBounds()
        );
//        ObjectiveFunctionOptimizer objectiveFunctionOptimizer = new ObjectiveFunctionOptimizer(getRandomVectorGenerator(fiducialSet));
        BOBYQAOptimizer  objectiveFunctionOptimizer = new BOBYQAOptimizer(5);
        PointValuePair optimize = objectiveFunctionOptimizer.optimize(
            GoalType.MAXIMIZE,
//            new ObjectiveFunction(adapter),
            new ObjectiveFunction(getObjectiveFunction(fiducialSet)),
//            new ObjectiveFunctionGradient(getObjectiveFunctionGradient(fiducialSet, adapter)),
            new InitialGuess(getInitialGuess(fiducialSet)),
            MaxEval.unlimited(),
            MaxIter.unlimited()
        );
//        return new PointValuePair(getBoundedValues(optimize.getPoint()), optimize.getValue());
        return optimize;
//        return new PointValuePair(adapter.unboundedToBounded(optimize.getPoint()), optimize.getValue());
    }

    protected abstract Rigid2DMaxLikelihoodObjectiveFunction getObjectiveFunction(FiducialSet fiducialSet);
    protected abstract MultivariateVectorFunction getObjectiveFunctionGradient(FiducialSet fiducialSet, MultivariateFunctionMappingAdapter adapter);
    protected abstract double[] getInitialGuess(FiducialSet fiducialSet);
    protected abstract int getNParameters();
    protected abstract double[] getParametersLowerBounds();
    protected abstract double[] getParametersUpperBounds();
    protected abstract RandomVectorGenerator getRandomVectorGenerator(FiducialSet fiducialSet);
    protected abstract double[] getBoundedValues(double[] point);
}
