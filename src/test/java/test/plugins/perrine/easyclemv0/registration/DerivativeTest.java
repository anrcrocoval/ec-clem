//package test.plugins.perrine.easyclemv0.registration;
//
//import fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
//import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
//import org.junit.jupiter.api.Disabled;
//
//import javax.inject.Inject;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@Disabled
//public class DerivativeTest {
//
//    private TestFiducialSetFactory testFiducialSetFactory;
//    private Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient gradient;
//    private MultivariateFunctionMappingAdapter function;
//    private Rigid2DGeneralMaxLikelihoodObjectiveFunction function2;
//
//    public DerivativeTest() {
//        DaggerDerivativeTestComponent.create().inject(this);
//         function = new MultivariateFunctionMappingAdapter(
//             new Rigid2DGeneralMaxLikelihoodObjectiveFunction(
//                 testFiducialSetFactory.getSimpleRotationFiducialSet2D()
//             ),
//             new double[] {
//                 Double.NEGATIVE_INFINITY,
//                 Double.NEGATIVE_INFINITY,
//                 0,
//                 0,
//                 0,
//                 0
//             },
//             new double[] {
//                 Double.POSITIVE_INFINITY,
//                 Double.POSITIVE_INFINITY,
//                 2 * Math.PI,
//                 Double.POSITIVE_INFINITY,
//                 Double.POSITIVE_INFINITY,
//                 Double.POSITIVE_INFINITY
//             }
//        );
//        function2 = new Rigid2DGeneralMaxLikelihoodObjectiveFunction(
//            testFiducialSetFactory.getSimpleRotationFiducialSet2D()
//        );
//        gradient = new Rigid2DGeneralMaxLikelihoodObjectiveFunctionGradient(
//            testFiducialSetFactory.getSimpleRotationFiducialSet2D(),
//            function
//        );
//    }
//
////    @Test
////    void testDerivative() {
////        double[] params = new double[] {1, 1, 1, 1, 1, 1};
////        double[] epsilonArray = new double[6];
////        double epsilon = 0.00001;
////        double[] plusEpsilon = new double[6];
////        double[] minusEpsilon = new double[6];
////        System.out.println(Arrays.toString(function.unboundedToBounded(params)));
////        double[] derivativeArray = function2.getDerivativeStructure().getAllDerivatives();
////        assertEquals(function.value(params), function2.getDerivativeStructure().getValue(), 0.00001);
////        for(int i = 0; i < params.length; i++) {
////            epsilonArray[i] += epsilon;
////            derivativeArray[i] += 1;
////            Arrays.setAll(plusEpsilon, j -> params[j] + epsilonArray[j]);
////            Arrays.setAll(minusEpsilon, j -> params[j] - epsilonArray[j]);
////            double expected = (function.value(plusEpsilon) - function.value(minusEpsilon)) / (2 * epsilon);
//////            assertEquals(expected, gradient.value(params)[i], 0.00001);
////            assertEquals(expected, derivativeArray[i], 0.00001);
////            epsilonArray[i] = 0;
////        }
////    }
//
//    @Inject
//    public void setFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
//        this.testFiducialSetFactory = testFiducialSetFactory;
//    }
//}
