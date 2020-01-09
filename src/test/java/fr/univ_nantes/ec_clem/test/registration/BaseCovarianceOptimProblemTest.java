package fr.univ_nantes.ec_clem.test.registration;

import Jama.Matrix;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.BaseCovarianceOptimProblem;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class BaseCovarianceOptimProblemTest {
    private TestFiducialSetFactory testFiducialSetFactory;

    public BaseCovarianceOptimProblemTest() {
        DaggerBaseCovarianceOptimProblemTestComponent.create().inject(this);
    }

    @Test
    void testObjectiveValue2D() {
        FiducialSet identityFiducialSet = testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100();
        BaseCovarianceOptimProblem subjectUnderTest = new BaseCovarianceOptimProblem(identityFiducialSet, new Similarity(
            Matrix.identity(2, 2),
            new Matrix(new double[][] {
                { 0 },
                { 0 }
            }),
            Matrix.identity(2, 2)
        ));
        double[] point = new double[]{ 0.1d, 0, 0, 0.1d };
        assertEquals(48.88197, subjectUnderTest.getObjectiveValue(point), 0.0001);
    }

    @Test
    void testObjectiveValue3D() {
        FiducialSet identityFiducialSet = testFiducialSetFactory.getIdentityFiducialSet3D();
        BaseCovarianceOptimProblem subjectUnderTest = new BaseCovarianceOptimProblem(identityFiducialSet, new Similarity(
            Matrix.identity(3, 3),
            new Matrix(new double[][] {
                { 0 },
                { 0 },
                { 0 }
            }),
            Matrix.identity(3, 3)
        ));
        double[] point = new double[]{ 0.1d, 0, 0, 0, 0.1d, 0, 0, 0, 0.1d };
        assertEquals(34.98252, subjectUnderTest.getObjectiveValue(point), 0.0001);
    }

    @Test
    void testObjectiveGradient2D() {
        BaseCovarianceOptimProblem baseOptimProblem = new BaseCovarianceOptimProblem(
            testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100(),
            new Similarity(
                Matrix.identity(2, 2),
                new Matrix(new double[][] {
                    { 0 },
                    { 0 }
                }),
                Matrix.identity(2, 2)
            )
        );
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
            assertEquals(expected, derivativeArray[i], 1e-5);
            epsilonArray[i] = 0;
        }
    }

    @Test
    void testObjectiveGradient3D() {
        BaseCovarianceOptimProblem baseOptimProblem = new BaseCovarianceOptimProblem(
            testFiducialSetFactory.getIdentityFiducialSet3D(),
            new Similarity(
                Matrix.identity(3, 3),
                new Matrix(new double[][] {
                    { 0 },
                    { 0 },
                    { 0 }
                }),
                Matrix.identity(3, 3)
            )
        );
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
            assertEquals(expected, derivativeArray[i], 1e-5);
            epsilonArray[i] = 0;
        }
    }

    @Test
    void testObjectivehessian3D() {
        BaseCovarianceOptimProblem baseOptimProblem = new BaseCovarianceOptimProblem(
            testFiducialSetFactory.getIdentityFiducialSet3D(),
            new Similarity(
                Matrix.identity(3, 3),
                new Matrix(new double[][] {
                    { 0 },
                    { 0 },
                    { 0 }
                }),
                Matrix.identity(3, 3)
            )
        );
        double[] point = baseOptimProblem.getStartingPoint();

        double[] epsilonArray = new double[baseOptimProblem.getNParameters()];
        double[] hArray = new double[baseOptimProblem.getNParameters()];
        double epsilon = 0.00001;

        double[] plusEpsilonPlusH = new double[baseOptimProblem.getNParameters()];
        double[] minusEpsilonPlusH = new double[baseOptimProblem.getNParameters()];
        double[] plusEpsilonMinusH = new double[baseOptimProblem.getNParameters()];
        double[] minusEpsilonMinusH = new double[baseOptimProblem.getNParameters()];

        double[] derivativeArray = baseOptimProblem.getObjectiveHessian(point);
        int count = 0;
        for(int i = 0; i < baseOptimProblem.getNParameters(); i++) {
            epsilonArray[i] += epsilon;
            for(int j = 0; j <= i; j++) {
                hArray[j] += epsilon;
                Arrays.setAll(plusEpsilonPlusH, k -> point[k] + epsilonArray[k] + hArray[k]);
                Arrays.setAll(plusEpsilonMinusH, k -> point[k] + epsilonArray[k] - hArray[k]);
                Arrays.setAll(minusEpsilonPlusH, k -> point[k] - epsilonArray[k] + hArray[k]);
                Arrays.setAll(minusEpsilonMinusH, k -> point[k] - epsilonArray[k] - hArray[k]);
                double expected = (
                    baseOptimProblem.getObjectiveValue(plusEpsilonPlusH)
                    - baseOptimProblem.getObjectiveValue(plusEpsilonMinusH)
                    - baseOptimProblem.getObjectiveValue(minusEpsilonPlusH)
                    + baseOptimProblem.getObjectiveValue(minusEpsilonMinusH)
                ) / (4 * epsilon * epsilon);
                assertEquals(expected, derivativeArray[count], 1e-5);
                hArray[j] = 0;
                count++;
            }
            epsilonArray[i] = 0;
        }
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }
}
