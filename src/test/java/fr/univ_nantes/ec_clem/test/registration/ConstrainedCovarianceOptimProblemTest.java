package fr.univ_nantes.ec_clem.test.registration;

import Jama.Matrix;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.isotropic.ConstrainedCovarianceOptimProblem;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;
import javax.inject.Inject;
import java.lang.reflect.Array;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class ConstrainedCovarianceOptimProblemTest {

    private TestFiducialSetFactory testFiducialSetFactory;

    public ConstrainedCovarianceOptimProblemTest() {
        DaggerConstrainedCovarianceOptimProblemTestComponent.create().inject(this);
    }

    @Test
    void getConstraint2D() {
        ConstrainedCovarianceOptimProblem subjectUnderTest = new ConstrainedCovarianceOptimProblem(
            testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100(), new Similarity(
            Matrix.identity(2, 2),
            new Matrix(new double[][] {
                { 0 },
                { 0 }
            }),
            Matrix.identity(2, 2)
        ));
        assertEquals(subjectUnderTest.getNConstraints(), 2);
        assertEquals(
            subjectUnderTest.getConstraints(new double[] {
                11, 12, 21, 22
            }),
            new double[] {
                -66,
                594
            }
        );
    }

    @Test
    void getConstraint3D() {
        ConstrainedCovarianceOptimProblem subjectUnderTest = new ConstrainedCovarianceOptimProblem(
            testFiducialSetFactory.getIdentityFiducialSet3D(), new Similarity(
            Matrix.identity(3, 3),
            new Matrix(new double[][] {
                { 0 },
                { 0 },
                { 0 }
            }),
            Matrix.identity(3, 3)
        ));
        assertEquals(subjectUnderTest.getNConstraints(), 5);
        assertEquals(
            subjectUnderTest.getConstraints(new double[] {
                11, 12, 13, 21, 22, 23, 31, 32, 33
            }),
            new double[] {
                11 * 11 + 21 * 21 + 31 * 31 - 12 * 12 - 22 * 22 - 32 * 32,
                11 * 11 + 21 * 21 + 31 * 31 - 13 * 13 - 23 * 23 - 33 * 33,
                12 * 11 + 22 * 21 + 32 * 31,
                13 * 11 + 23 * 21 + 33 * 31,
                13 * 12 + 23 * 22 + 33 * 32
            }
        );
    }

    @Test
    void getConstraintsJacobian() {
        ConstrainedCovarianceOptimProblem subjectUnderTest = new ConstrainedCovarianceOptimProblem(
            testFiducialSetFactory.getIdentityFiducialSet3D(), new Similarity(
            Matrix.identity(3, 3),
            new Matrix(new double[][] {
                { 0 },
                { 0 },
                { 0 }
            }),
            Matrix.identity(3, 3)
        ));
        assertEquals(
            subjectUnderTest.getConstraintsJacobian(new double[] {
                11, 12, 13, 21, 22, 23, 31, 32, 33
            }),
            new double[] {
                2 * 11, -2 * 12, 0, 2 * 21, -2 * 22, 0, 2 * 31, -2 * 32, 0,
                2 * 11, 0, -2 * 13, 2 * 21, 0, -2 * 23, 2 * 31, 0, -2 * 33,
                12, 11, 0, 22, 21, 0, 32, 31, 0,
                13, 0, 11, 23, 0, 21, 33, 0, 31,
                0, 13, 12, 0, 23, 22, 0, 33, 32
            }
        );
    }

    @Test
    void getConstraintsHessian() {
        ConstrainedCovarianceOptimProblem subjectUnderTest = new ConstrainedCovarianceOptimProblem(
            testFiducialSetFactory.getIdentityFiducialSet3D(), new Similarity(
            Matrix.identity(3, 3),
            new Matrix(new double[][] {
                { 0 },
                { 0 },
                { 0 }
            }),
            Matrix.identity(3, 3)
        ));
        double[][] constraintsHessian = subjectUnderTest.getConstraintsHessian(new double[]{
            11, 12, 13, 21, 22, 23, 31, 32, 33
        });
//        System.out.println(Arrays.toString(constraintsHessian[0]));
        assertEquals(
            constraintsHessian[0],
            new double[] {
                2,
                0, -2,
                0, 0, 0,
                0, 0, 0, 2,
                0, 0, 0, 0, -2,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 2,
                0, 0, 0, 0, 0, 0, 0, -2,
                0, 0, 0, 0, 0, 0, 0, 0, 0
            });
        assertEquals(
            constraintsHessian[1],
            new double[] {
                2,
                0, 0,
                0, 0, -2,
                0, 0, 0, 2,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, -2,
                0, 0, 0, 0, 0, 0, 2,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, -2
            });
        assertEquals(
            constraintsHessian[2],
            new double[] {
                0,
                1, 0,
                0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0
            });
        assertEquals(
            constraintsHessian[3],
            new double[] {
                0,
                0, 0,
                1, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 1, 0, 0,
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 0
            });
        assertEquals(
            constraintsHessian[4],
            new double[] {
                0,
                0, 0,
                0, 1, 0,
                0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 0,
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 1, 0
            });
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }
}
