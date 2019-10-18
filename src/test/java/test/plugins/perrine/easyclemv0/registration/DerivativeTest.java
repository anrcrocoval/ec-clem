package test.plugins.perrine.easyclemv0.registration;

import fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.Rigid2DGeneralMaxLikelihoodComputer;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.Rigid2DGeneralMaxLikelihoodObjectiveFunction;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient;
import javax.inject.Inject;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DerivativeTest {

    private TestFiducialSetFactory testFiducialSetFactory;
    private Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient gradient;
    private MultivariateFunctionMappingAdapter function;
    private Rigid2DGeneralMaxLikelihoodObjectiveFunction function2;

    public DerivativeTest() {
        DaggerDerivativeTestComponent.create().inject(this);
//         function = new MultivariateFunctionMappingAdapter(
//             new Rigid2DGeneralMaxLikelihoodObjectiveFunction(
//                 testFiducialSetFactory.getSimpleRotationFiducialSet2D()
//             ),
//             new double[] {
//                 -10000,
//                 -10000,
//                 0,
//                 0.00001,
//                 0,
//                 0.00001
//             },
//             new double[] {
//                 10000,
//                 10000,
//                 2 * Math.PI,
//                 1000,
//                 1000,
//                 1000
//             }
//        );
        function2 = new Rigid2DGeneralMaxLikelihoodObjectiveFunction(
            testFiducialSetFactory.getSimpleRotationFiducialSet2D()
        );
        gradient = new Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient(
            testFiducialSetFactory.getSimpleRotationFiducialSet2D(),
            function
        );
    }

    @Test
    void testDerivative() {
        double[] params = new double[] {1, 1, 1, 1, 1, 1};
        double[] epsilonArray = new double[6];
        double epsilon = 0.00001;
        double[] plusEpsilon = new double[6];
        double[] minusEpsilon = new double[6];
        for(int i = 0; i < params.length; i++) {
            epsilonArray[i] += epsilon;
            Arrays.setAll(plusEpsilon, j -> params[j] + epsilonArray[j]);
            Arrays.setAll(minusEpsilon, j -> params[j] - epsilonArray[j]);
            double expected = (function2.value(plusEpsilon) - function2.value(minusEpsilon)) / (2 * epsilon);
            assertEquals(expected, gradient.value(params)[i], 0.00001);
            epsilonArray[i] = 0;
        }
    }

    @Inject
    public void setFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }
}
