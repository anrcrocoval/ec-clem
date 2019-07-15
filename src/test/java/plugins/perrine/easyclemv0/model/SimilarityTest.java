package plugins.perrine.easyclemv0.model;

import Jama.Matrix;
import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.model.transformation.Similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimilarityTest {

    @Test
    void getMatrix() {
        Matrix R = new Matrix(
            new double[][] {
                { 0, -1, 0 },
                { 1, 0, 0 },
                { 0, 0, 1 }
            }
        );
        Matrix T = new Matrix(
            new double[][] {
                { 1 },
                { 2 },
                { 3 }
            }
        );
        Similarity S = new Similarity(R, T, Matrix.identity(3,3).times(2));
        Matrix M = S.getHomogeneousMatrix();
        assertEquals(0, M.get(0, 0));
        assertEquals(2, M.get(1, 0));
        assertEquals(0, M.get(2, 0));
        assertEquals(0, M.get(3, 0));
        assertEquals(-2, M.get(0, 1));
        assertEquals(0, M.get(1, 1));
        assertEquals(0, M.get(2, 1));
        assertEquals(0, M.get(3, 1));
        assertEquals(0, M.get(0, 2));
        assertEquals(0, M.get(1, 2));
        assertEquals(2, M.get(2, 2));
        assertEquals(0, M.get(3, 2));
        assertEquals(1, M.get(0, 3));
        assertEquals(2, M.get(1, 3));
        assertEquals(3, M.get(2, 3));
        assertEquals(1, M.get(3, 3));
    }
}
