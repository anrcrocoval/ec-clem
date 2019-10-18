package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.random.*;

import static org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES;
import static org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE;

public class ObjectiveFunctionOptimizer {

    private MultiStartMultivariateOptimizer multiStartMultivariateOptimizer;

    public ObjectiveFunctionOptimizer(RandomVectorGenerator generator) {
        multiStartMultivariateOptimizer = new MultiStartMultivariateOptimizer(
                new NonLinearConjugateGradientOptimizer(
                    FLETCHER_REEVES,
                        new SimpleValueChecker(0.000000001, 0.000000001)
                ),
                1000,
            generator
        );
    }

    public PointValuePair optimize(OptimizationData ... optData) {
        return multiStartMultivariateOptimizer.optimize(optData);
    }
}
