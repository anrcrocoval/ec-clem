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

import plugins.fr.univ_nantes.ec_clem.fixtures.fiducialset.TestFiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.fixtures.transformation.TestTransformationFactory;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.conjugate_gradient.ConjugateGradientRigid2DGeneralMaxLikelihoodComputer;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;
import javax.inject.Inject;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.testng.Assert.assertEquals;

class ConjugateGradientRigid2DGeneralMaxLikelihoodComputerTest {

    private TestFiducialSetFactory testFiducialSetFactory;
    private TestTransformationFactory testTransformationFactory;
    private ConjugateGradientRigid2DGeneralMaxLikelihoodComputer subjectUnderTest;

    public ConjugateGradientRigid2DGeneralMaxLikelihoodComputerTest() {
        DaggerConjugateGradientRigid2DGeneralMaxLikelihoodComputerTestComponent.create().inject(this);
    }

    @Test(enabled = false)
    void identity() {
        FiducialSet identityFiducialSet = testFiducialSetFactory.getIdentityFiducialSet2DWithNoise100_0_0_100();
        Similarity result = (Similarity) subjectUnderTest.compute(identityFiducialSet).getTransformation();
        assertEquals(2, result.getR().getRowDimension());
        assertEquals(2, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(identityFiducialSet.getSourceDataset(), identityFiducialSet.getTargetDataset(), result);
        assertEquals(1, result.getR().get(0, 0), 0.1);
        assertEquals(0, result.getR().get(1, 0), 0.1);
        assertEquals(0, result.getR().get(0, 1), 0.1);
        assertEquals(1, result.getR().get(1, 1), 0.1);

        assertEquals(2, result.getS().getRowDimension());
        assertEquals(2, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.1);
        assertEquals(0, result.getS().get(1, 0), 0.1);
        assertEquals(0, result.getS().get(0, 1), 0.1);
        assertEquals(1, result.getS().get(1, 1), 0.1);

        assertEquals(2, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 1);
        assertEquals(0, result.getT().get(1, 0), 1);
    }

    @Test(enabled = false)
    void simpleRotation() {
        FiducialSet simpleRotationFiducialSet = testFiducialSetFactory.getSimpleRotationFiducialSet2D();
        Similarity result = (Similarity) subjectUnderTest.compute(simpleRotationFiducialSet).getTransformation();
        assertEquals(2, result.getR().getRowDimension());
        assertEquals(2, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(simpleRotationFiducialSet.getSourceDataset(), simpleRotationFiducialSet.getTargetDataset(), result);
        assertEquals(cos(38), result.getR().get(0, 0), 0.00001);
        assertEquals(sin(38), result.getR().get(1, 0), 0.00001);
        assertEquals(-sin(38), result.getR().get(0, 1), 0.00001);
        assertEquals(cos(38), result.getR().get(1, 1), 0.00001);

        assertEquals(2, result.getS().getRowDimension());
        assertEquals(2, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.00001);
        assertEquals(0, result.getS().get(1, 0), 0.00001);
        assertEquals(0, result.getS().get(0, 1), 0.00001);
        assertEquals(1, result.getS().get(1, 1), 0.00001);

        assertEquals(2, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(0, result.getT().get(0, 0), 0.00001);
        assertEquals(0, result.getT().get(1, 0), 0.00001);
    }

    @Test(enabled = false)
    void simpleTranslation() {
        FiducialSet simpleTranslationFiducialSet = testFiducialSetFactory.getSimpleTranslationFiducialSet2D();
        Similarity result = (Similarity) subjectUnderTest.compute(simpleTranslationFiducialSet).getTransformation();
        assertEquals(2, result.getR().getRowDimension());
        assertEquals(2, result.getR().getColumnDimension());
        checkDetIsOne(result);
        checkExpectedResidualIsZero(simpleTranslationFiducialSet.getSourceDataset(), simpleTranslationFiducialSet.getTargetDataset(), result);
        assertEquals(1, result.getR().get(0, 0), 0.000001);
        assertEquals(0, result.getR().get(1, 0), 0.000001);
        assertEquals(0, result.getR().get(0, 1), 0.000001);
        assertEquals(1, result.getR().get(1, 1), 0.000001);

        assertEquals(2, result.getS().getRowDimension());
        assertEquals(2, result.getS().getColumnDimension());
        assertEquals(1, result.getS().get(0, 0), 0.000001);
        assertEquals(0, result.getS().get(1, 0), 0.000001);
        assertEquals(0, result.getS().get(0, 1), 0.000001);
        assertEquals(1, result.getS().get(1, 1), 0.000001);

        assertEquals(2, result.getT().getRowDimension());
        assertEquals(1, result.getT().getColumnDimension());
        assertEquals(1, result.getT().get(0, 0), 0.000001);
        assertEquals(1, result.getT().get(1, 0), 0.00001);
    }

    private void checkDetIsOne(Similarity result) {
        assertEquals(1, result.getR().det(), 0.0000001);
    }

    private void checkExpectedResidualIsZero(Dataset sourceDataset, Dataset targetDataset, Similarity result) {
        Point expected = new Dataset(
                targetDataset.getMatrix().minus(
                        sourceDataset.getHomogeneousMatrixRight().times(result.getMatrixRight().transpose())
                ), PointType.FIDUCIAL
        ).getBarycentre();
        for(int i = 0; i < expected.getDimension(); i++) {
            assertEquals(0, expected.get(i), 0.01);
        }
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
    public void setSubjectUnderTest(ConjugateGradientRigid2DGeneralMaxLikelihoodComputer subjectUnderTest) {
        this.subjectUnderTest = subjectUnderTest;
    }
}
