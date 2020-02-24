package fr.univ_nantes.ec_clem.test.error.ellipse.rigid.dimension3;

import Jama.Matrix;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid.dimension3.RotationParameters3D;
import plugins.fr.univ_nantes.ec_clem.fixtures.matrix.TestMatrixFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

public class RotationParameters3DTest {

    private RotationParameters3D subjectUnderTest;
    private TestMatrixFactory testMatrixFactory;

    public RotationParameters3DTest() {
        DaggerRotationParameters3DTestComponent.create().inject(this);
    }

    @Test
    void zyzEulerAnglesTest() {
        Matrix zyz3DRotationMatrix = testMatrixFactory.getZYZ3DRotationMatrix(38 * Math.PI / 180, 12 * Math.PI / 180, 64 * Math.PI / 180);
        double[] eulerParameters = subjectUnderTest.getZYZEulerParameters(new Similarity(
            zyz3DRotationMatrix,
            new Matrix(new double[][]{
                {0}, {0}, {0}
            }),
            Matrix.identity(3, 3)
        ));
        assertEquals(eulerParameters[0],38 * Math.PI / 180);
        assertEquals(eulerParameters[1], 12 * Math.PI / 180);
        assertEquals(eulerParameters[2], 64 * Math.PI / 180);
    }

    @Inject
    public void setSubjectUnderTest(RotationParameters3D subjectUnderTest) {
        this.subjectUnderTest = subjectUnderTest;
    }

    @Inject
    public void setTestMatrixFactory(TestMatrixFactory testMatrixFactory) {
        this.testMatrixFactory = testMatrixFactory;
    }
}
