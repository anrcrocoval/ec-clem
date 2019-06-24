package plugins.perrine.easyclemv0.model;

import Jama.Matrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatasetTest {
    private Dataset subjectUnderTest;

    @BeforeEach
    void init() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(new Matrix(new double[][] {{ 1 }, { 2 }, { 3 }})));
        points.add(new Point(new Matrix(new double[][] {{ 4 }, { 5 }, { 6 }})));
        points.add(new Point(new Matrix(new double[][] {{ 7 }, { 8 }, { 9 }})));
        subjectUnderTest = new Dataset(points);
    }

    @Test
    void getBarycentreTest() {
        Point barycentre = subjectUnderTest.getBarycentre();
        assertEquals(4, barycentre.getmatrix().get(0, 0));
        assertEquals(5, barycentre.getmatrix().get(1, 0));
        assertEquals(6, barycentre.getmatrix().get(2, 0));
        assertEquals(3, barycentre.getDimension());
    }

    @Test
    void substractBarycentre() {
        subjectUnderTest.substractBarycentre();
        assertEquals(-3,subjectUnderTest.getMatrix().get(0, 0));
        assertEquals(0, subjectUnderTest.getMatrix().get(1, 0));
        assertEquals(3, subjectUnderTest.getMatrix().get(2, 0));
        assertEquals(-3, subjectUnderTest.getMatrix().get(0, 1));
        assertEquals(0, subjectUnderTest.getMatrix().get(1, 1));
        assertEquals(3, subjectUnderTest.getMatrix().get(2, 1));
        assertEquals(-3, subjectUnderTest.getMatrix().get(0, 2));
        assertEquals(0, subjectUnderTest.getMatrix().get(1, 2));
        assertEquals(3, subjectUnderTest.getMatrix().get(2, 2));
        assertEquals(3, subjectUnderTest.getN());
        assertEquals(3, subjectUnderTest.getDimension());
    }

    @Test
    void getMeanNormTest() {
        double meanNorm = subjectUnderTest.getMeanNorm();
        assertEquals(8.815, meanNorm, 0.0001);
    }
}
