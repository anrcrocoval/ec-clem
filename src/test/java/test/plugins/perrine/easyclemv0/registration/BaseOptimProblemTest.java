package test.plugins.perrine.easyclemv0.registration;

import fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.general.BaseOptimProblem;

import javax.inject.Inject;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseOptimProblemTest {

    private TestFiducialSetFactory testFiducialSetFactory;
    private BaseOptimProblem subjectUnderTest;

    public BaseOptimProblemTest() {
        DaggerBaseOptimProblemTestComponent.create().inject(this);
    }

    @Test
    void testObjectiveValue() {
        FiducialSet identityFiducialSet = testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100();
        subjectUnderTest = new BaseOptimProblem(identityFiducialSet);
        double[] point = new double[]{ 0, 0, 0, 0.1d, 0, 0, 0.1d };
        assertEquals(48.88197, subjectUnderTest.getObjectiveValue(point), 0.0001);
    }

    @Test
    @Disabled
    void testObjectiveGradient() throws ExecutionException, InterruptedException {
        FiducialSet identityFiducialSet = testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100();
        subjectUnderTest = new BaseOptimProblem(identityFiducialSet);
        double[] point = new double[]{ 0, 0, 0, 0.1d, 0, 0, 0.1d };
        double[] gradient = subjectUnderTest.getObjectiveGradient(point);
        assertEquals(7, gradient.length);
        assertEquals(0.0297542, gradient[0], 0.000001);
        assertEquals(-0.0229752, gradient[1], 0.000001);
        assertEquals(-0.9437350, gradient[2], 0.000001);
        assertEquals(-247.3931296, gradient[3], 0.000001);
        assertEquals(-35.3777881, gradient[4], 0.000001);
        assertEquals(-35.3777881, gradient[5], 0.000001);
        assertEquals(-74.5398387, gradient[6], 0.000001);
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }
}
