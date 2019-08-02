/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package test.plugins.perrine.easyclemv0.registration;

import org.junit.jupiter.api.Test;
import plugins.perrine.easyclemv0.registration.SimilarityTransformationComputer;
import test.plugins.perrine.easyclemv0.fiducialset.TestFiducialSetFactory;
import test.plugins.perrine.easyclemv0.transformation.TestTransformationFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.transformation.Similarity;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

import javax.inject.Inject;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimilarityTransformationComputerTest {

    private TestFiducialSetFactory testFiducialSetFactory;
    private TestTransformationFactory testTransformationFactory;
    private SimilarityTransformationComputer subjectUnderTest;

    public SimilarityTransformationComputerTest() {
        DaggerSimilarityTransformationComputerTestComponent.create().inject(this);
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }

    @Inject
    public void setTestTransformationFactory(TestTransformationFactory testTransformationFactory) {
        this.testTransformationFactory = testTransformationFactory;
    }

    @Inject
    public void setSubjectUnderTest(SimilarityTransformationComputer subjectUnderTest) {
        this.subjectUnderTest = subjectUnderTest;
    }

    @Test
    void simpleRotation() {
        FiducialSet simpleRotationFiducialSet = testFiducialSetFactory.getSimpleRotationFiducialSet();
        Similarity result = subjectUnderTest.compute(simpleRotationFiducialSet);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(simpleRotationFiducialSet.getSourceDataset(), simpleRotationFiducialSet.getTargetDataset(), result);
        assertEquals(0, result.getR().get(0, 0), 0.0000000001);
        assertEquals(1, result.getR().get(1, 0), 0.0000000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000000001);
        assertEquals(-1, result.getR().get(0, 1), 0.0000000001);
        assertEquals(0, result.getR().get(1, 1), 0.0000000001);
        assertEquals(0, result.getR().get(2, 1), 0.0000000001);
        assertEquals(0, result.getR().get(0, 2), 0.0000000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000000001);
        assertEquals(1, result.getR().get(2, 2), 0.0000000001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(2, result.getS().get(0, 0), 0.0000000001);
        assertEquals(0, result.getS().get(1, 0), 0.0000000001);
        assertEquals(0, result.getS().get(2, 0), 0.0000000001);
        assertEquals(0, result.getS().get(0, 1), 0.0000000001);
        assertEquals(2, result.getS().get(1, 1), 0.0000000001);
        assertEquals(0, result.getS().get(2, 1), 0.0000000001);
        assertEquals(0, result.getS().get(0, 2), 0.0000000001);
        assertEquals(0, result.getS().get(1, 2), 0.0000000001);
        assertEquals(2, result.getS().get(2, 2), 0.0000000001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.0000000001);
        assertEquals(0, result.getT().get(1, 0), 0.0000000001);
        assertEquals(0, result.getT().get(2, 0), 0.0000000001);
    }

    @Test
    void randomSimpleRotation() {
        double angle = 38;
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation(angle);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomFromTransformation(simpleRotationTransformation, 5);
        Similarity result = subjectUnderTest.compute(randomFromTransformationFiducialSet);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(randomFromTransformationFiducialSet.getSourceDataset(), randomFromTransformationFiducialSet.getTargetDataset(), result);
        assertEquals(cos(angle), result.getR().get(0, 0), 0.0000000001);
        assertEquals(sin(angle), result.getR().get(1, 0), 0.0000000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000000001);
        assertEquals(-1 * sin(angle), result.getR().get(0, 1), 0.0000000001);
        assertEquals(cos(angle), result.getR().get(1, 1), 0.0000000001);
        assertEquals(0, result.getR().get(2, 1), 0.0000000001);
        assertEquals(0, result.getR().get(0, 2), 0.0000000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000000001);
        assertEquals(1, result.getR().get(2, 2), 0.0000000001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.0000000001);
        assertEquals(0, result.getS().get(1, 0), 0.0000000001);
        assertEquals(0, result.getS().get(2, 0), 0.0000000001);
        assertEquals(0, result.getS().get(0, 1), 0.0000000001);
        assertEquals(1, result.getS().get(1, 1), 0.0000000001);
        assertEquals(0, result.getS().get(2, 1), 0.0000000001);
        assertEquals(0, result.getS().get(0, 2), 0.0000000001);
        assertEquals(0, result.getS().get(1, 2), 0.0000000001);
        assertEquals(1, result.getS().get(2, 2), 0.0000000001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.0000000001);
        assertEquals(0, result.getT().get(1, 0), 0.0000000001);
        assertEquals(0, result.getT().get(2, 0), 0.0000000001);
    }

    @Test
    void simpleTranslation() {
        FiducialSet simpleTranslationFiducialSet = testFiducialSetFactory.getSimpleTranslationFiducialSet();
        Similarity result = subjectUnderTest.compute(simpleTranslationFiducialSet);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(simpleTranslationFiducialSet.getSourceDataset(), simpleTranslationFiducialSet.getTargetDataset(), result);
        assertEquals(1, result.getR().get(0, 0), 0.0000000001);
        assertEquals(0, result.getR().get(1, 0), 0.0000000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000000001);
        assertEquals(0, result.getR().get(0, 1), 0.0000000001);
        assertEquals(1, result.getR().get(1, 1), 0.0000000001);
        assertEquals(0, result.getR().get(2, 1), 0.0000000001);
        assertEquals(0, result.getR().get(0, 2), 0.0000000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000000001);
        assertEquals(1, result.getR().get(2, 2), 0.0000000001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.0000000001);
        assertEquals(0, result.getS().get(1, 0), 0.0000000001);
        assertEquals(0, result.getS().get(2, 0), 0.0000000001);
        assertEquals(0, result.getS().get(0, 1), 0.0000000001);
        assertEquals(1, result.getS().get(1, 1), 0.0000000001);
        assertEquals(0, result.getS().get(2, 1), 0.0000000001);
        assertEquals(0, result.getS().get(0, 2), 0.0000000001);
        assertEquals(0, result.getS().get(1, 2), 0.0000000001);
        assertEquals(1, result.getS().get(2, 2), 0.0000000001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(1, result.getT().get(0, 0), 0.0000001);
        assertEquals(1, result.getT().get(1, 0), 0.0000000001);
        assertEquals(1, result.getT().get(2, 0), 0.0000000001);
    }

    @Test
    void complexRotation() {
        FiducialSet complexRotationFiducialSet = testFiducialSetFactory.getComplexRotationFiducialSet();
        Similarity result = subjectUnderTest.compute(complexRotationFiducialSet);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(complexRotationFiducialSet.getSourceDataset(), complexRotationFiducialSet.getTargetDataset(), result);
        assertEquals(0, result.getR().get(0, 0), 0.0000001);
        assertEquals(1, result.getR().get(1, 0), 0.0000001);
        assertEquals(0, result.getR().get(2, 0), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(0, 1), 0.0000001);
        assertEquals(0, result.getR().get(1, 1), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(2, 1), 0.0000001);
        assertEquals(-0.7071068, result.getR().get(0, 2), 0.0000001);
        assertEquals(0, result.getR().get(1, 2), 0.0000001);
        assertEquals(0.7071068, result.getR().get(2, 2), 0.0000001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.0000001);
        assertEquals(0, result.getS().get(1, 0), 0.0000001);
        assertEquals(0, result.getS().get(2, 0), 0.0000001);
        assertEquals(0, result.getS().get(0, 1), 0.0000001);
        assertEquals(1, result.getS().get(1, 1), 0.0000001);
        assertEquals(0, result.getS().get(2, 1), 0.0000001);
        assertEquals(0, result.getS().get(0, 2), 0.0000001);
        assertEquals(0, result.getS().get(1, 2), 0.0000001);
        assertEquals(1, result.getS().get(2, 2), 0.0000001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.0000001);
        assertEquals(0, result.getT().get(1, 0), 0.0000001);
        assertEquals(0, result.getT().get(2, 0), 0.000001);
    }

    @Test
    void translationRotationScaling() {
        FiducialSet translationRotationScalingFiducialSet = testFiducialSetFactory.getTranslationRotationScalingFiducialSet();
        Similarity result = subjectUnderTest.compute(translationRotationScalingFiducialSet);
        assertEquals(3, result.getR().getRowDimension());
        assertEquals(3, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(translationRotationScalingFiducialSet.getSourceDataset(), translationRotationScalingFiducialSet.getTargetDataset(), result);
        assertEquals(0.82310, result.getR().get(0, 0), 0.00001);
        assertEquals(-0.56789, result.getR().get(1, 0), 0.00001);
        assertEquals(0, result.getR().get(2, 0), 0.00001);
        assertEquals(0.56789, result.getR().get(0, 1), 0.00001);
        assertEquals(0.82310, result.getR().get(1, 1), 0.00001);
        assertEquals(0, result.getR().get(2, 1), 0.00001);
        assertEquals(0, result.getR().get(0, 2), 0.00001);
        assertEquals(0, result.getR().get(1, 2), 0.00001);
        assertEquals(1, result.getR().get(2, 2), 0.00001);

        assertEquals(3, result.getS().getRowDimension());
        assertEquals(3, result.getS().getColumnDimension());
        assertEquals(1.97705, result.getS().get(0, 0), 0.00001);
        assertEquals(0, result.getS().get(1, 0), 0.00001);
        assertEquals(0, result.getS().get(2, 0), 0.00001);
        assertEquals(0, result.getS().get(0, 1), 0.00001);
        assertEquals(1.77642, result.getS().get(1, 1), 0.00001);
        assertEquals(0, result.getS().get(2, 1), 0.00001);
        assertEquals(0, result.getS().get(0, 2), 0.00001);
        assertEquals(0, result.getS().get(1, 2), 0.00001);
        assertEquals(1, result.getS().get(2, 2), 0.00001);

        assertEquals(3, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(-256.97393, result.getT().get(0, 0), 0.0001);
        assertEquals(287.22428, result.getT().get(1, 0), 0.0001);
        assertEquals(0, result.getT().get(2, 0), 0.00001);
    }

    private void checkDetIsOne(Similarity result) {
        assertEquals(1, result.getR().det(), 0.0000000001);
    }

    private void checkExpectedResidualIsZero(Dataset sourceDataset, Dataset targetDataset, Similarity result) {
        Point expected = new Dataset(
                targetDataset.getMatrix().minus(
                        sourceDataset.getHomogeneousMatrixRight().times(result.getMatrixRight().transpose())
                )
        ).getBarycentre();
        for(int i = 0; i < expected.getDimension(); i++) {
            assertEquals(0, expected.get(i), 0.0000001);
        }
    }
}
