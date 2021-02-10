package fr.univ_nantes.ec_clem.test.error.ellipse;

import Jama.Matrix;
import org.testng.annotations.Test;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.ConfidenceEllipseFactory;
import plugins.perrine.easyclemv0.ec_clem.error.ellipse.Ellipse;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

public class ConfidenceEllipseFactoryTest {

    private ConfidenceEllipseFactory subjectUnderTest;
    private TestFiducialSetFactory testFiducialSetFactory;

    public ConfidenceEllipseFactoryTest() {
        DaggerConfidenceEllipseFactoryTestComponent.create().inject(this);
    }

    @Test
    void getFromTest() {
        Point zTarget = new Point(new double[] {
            0, 0
        });
        FiducialSet fiducialSet = testFiducialSetFactory.getSimpleRotationFiducialSet2D();
        Matrix covariance = new Matrix(new double[][] {
            { 200 , 0 },
            { 0, 100 }
        });
        double alpha = 0.95;
        Ellipse ellipse = subjectUnderTest.getFrom(zTarget, fiducialSet, covariance, alpha);

        assertEquals(ellipse.getEigenValues()[0], 4457.644098096537, 1e-8);
        assertEquals(ellipse.getEigenValues()[1], 8915.288196193074, 1e-8);
        assertEquals(ellipse.getEigenVectors().get(0, 0), 0, 1e-8);
        assertEquals(ellipse.getEigenVectors().get(0, 1), 1, 1e-8);
        assertEquals(ellipse.getEigenVectors().get(1, 0), 1, 1e-8);
        assertEquals(ellipse.getEigenVectors().get(1, 1), 0, 1e-8);
    }

    @Inject
    public void setSubjectUnderTest(ConfidenceEllipseFactory subjectUnderTest) {
        this.subjectUnderTest = subjectUnderTest;
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }
}
