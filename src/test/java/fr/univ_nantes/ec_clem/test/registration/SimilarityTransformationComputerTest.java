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
package fr.univ_nantes.ec_clem.test.registration;

import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.fixtures.transformation.TestTransformationFactory;
import plugins.fr.univ_nantes.ec_clem.registration.SimilarityRegistrationParameterComputer;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.testng.Assert.assertEquals;

class SimilarityTransformationComputerTest {

    private TestFiducialSetFactory testFiducialSetFactory;
    private TestTransformationFactory testTransformationFactory;
    private SimilarityRegistrationParameterComputer subjectUnderTest;

    public SimilarityTransformationComputerTest() {
        DaggerSimilarityTransformationComputerTestComponent.create().inject(this);
    }

    @Inject
    public void setSubjectUnderTest(SimilarityRegistrationParameterComputer subjectUnderTest) {
        this.subjectUnderTest = subjectUnderTest;
    }

    @Inject
    public void setTestFiducialSetFactory(TestFiducialSetFactory testFiducialSetFactory) {
        this.testFiducialSetFactory = testFiducialSetFactory;
    }

    @Inject
    public void setTestTransformationFactory(TestTransformationFactory testTransformationFactory) {
        this.testTransformationFactory = testTransformationFactory;
    }

    @Test
    void simpleRotation() {
        FiducialSet simpleRotationFiducialSet = testFiducialSetFactory.getSimpleRotationFiducialSet3D();
        Similarity result = (Similarity) subjectUnderTest.compute(simpleRotationFiducialSet).getTransformation();
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
        int[] range = new int[] {256, 256, 256};
        Similarity simpleRotationTransformation = testTransformationFactory.getSimpleRotationTransformation3D(0, 0, angle);
        FiducialSet randomFromTransformationFiducialSet = testFiducialSetFactory.getRandomFromTransformation(simpleRotationTransformation, 10, range);
        Similarity result = (Similarity) subjectUnderTest.compute(randomFromTransformationFiducialSet).getTransformation();
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
        FiducialSet simpleTranslationFiducialSet = testFiducialSetFactory.getSimpleTranslationFiducialSet3D();
        Similarity result = (Similarity) subjectUnderTest.compute(simpleTranslationFiducialSet).getTransformation();
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
        FiducialSet complexRotationFiducialSet = testFiducialSetFactory.getComplexRotationFiducialSet3D();
        Similarity result = (Similarity) subjectUnderTest.compute(complexRotationFiducialSet).getTransformation();
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
        FiducialSet translationRotationScalingFiducialSet = testFiducialSetFactory.getTranslationRotationScalingFiducialSet3D();
        Similarity result = (Similarity) subjectUnderTest.compute(translationRotationScalingFiducialSet).getTransformation();
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
                ), PointType.FIDUCIAL
        ).getBarycentre();
        for(int i = 0; i < expected.getDimension(); i++) {
            assertEquals(0, expected.get(i), 0.0000001);
        }
    }
}
