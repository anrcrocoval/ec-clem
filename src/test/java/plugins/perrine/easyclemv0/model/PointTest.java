package plugins.perrine.easyclemv0.model;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PointTest {
    private Point subjectUnderTest;

    @Test
    void getDistance2DTest() {
        Matrix axis = new Matrix(new double[][] {
            { 3 }, { 0 }
        });
        Matrix pointOnTheAxis = new Matrix(2, 1, 0);
        subjectUnderTest = new Point(new Matrix(new double[][] {
            { 2 }, { 7 }
        }));
        assertEquals(Math.pow(7, 2), subjectUnderTest.getSquareDistance(axis, pointOnTheAxis));
    }

    @Test
    void getDistance3DTest() {
        Matrix axis = new Matrix(new double[][] {
            { 3 }, { 0 }, { 0 }
        });
        Matrix pointOnTheAxis = new Matrix(3, 1, 0);
        subjectUnderTest = new Point(new Matrix(new double[][] {
            { 1 }, { 1 }, { 1 }
        }));
        assertEquals(2, subjectUnderTest.getSquareDistance(axis, pointOnTheAxis));
    }
}
