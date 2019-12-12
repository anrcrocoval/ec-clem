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
package fr.univ_nantes.ec_clem.test.storage;

import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.sequence.DimensionSize;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceSize;
import plugins.fr.univ_nantes.ec_clem.storage.XmlFileReader;
import plugins.fr.univ_nantes.ec_clem.storage.XmlFileWriter;
import plugins.fr.univ_nantes.ec_clem.storage.XmlTransformationWriter;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;
import plugins.fr.univ_nantes.ec_clem.storage.XmlTransformationReader;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static plugins.fr.univ_nantes.ec_clem.storage.XmlTransformation.transformationElementName;

class XmlFileWriterTest {
    private File file = new File("XmlFileWriterTest.storage");
    private XmlFileReader xmlFileReader = new XmlFileReader();
    private XmlFileWriter xmlFileWriter;
    private XmlTransformationReader xmlReader = new XmlTransformationReader();
    private XmlTransformationWriter xmlWriter;
    private SequenceSize sourceSequenceSize = new SequenceSize();
    private SequenceSize targetSequenceSize = new SequenceSize();

    @BeforeTest
    @AfterTest
    private void clearFile() {
        file.delete();
    }

    @Test
    void writeAndRead() {
        Dataset sourceDataset = new Dataset(new double[][] {{ 1, 2, 0 }, { 2, 3, 0}, { 3, 4, 1 }}, PointType.FIDUCIAL);
        Dataset targetDataset = new Dataset(new double[][] {{ 1, 2, 0 }, { 2, 3, 0}, { 3, 4, 1 }}, PointType.FIDUCIAL);
        sourceSequenceSize.add(new DimensionSize(DimensionId.X, 10, 1));
        targetSequenceSize.add(new DimensionSize(DimensionId.X, 10, 1));
        TransformationSchema transformationSchema = new TransformationSchema(
            new FiducialSet(sourceDataset, targetDataset),
            TransformationType.SIMILARITY,
            NoiseModel.ISOTROPIC,
            sourceSequenceSize,
            targetSequenceSize
        );
        write(transformationSchema);
        TransformationSchema read = read();
        assertEquals(transformationSchema.getTransformationType(), read.getTransformationType());
        assertEquals(
            transformationSchema.getFiducialSet().getSourceDataset().getMatrix().getColumnDimension(),
            read.getFiducialSet().getSourceDataset().getMatrix().getColumnDimension());
    }

    private void write(TransformationSchema transformationSchema) {
        Document document = xmlFileReader.loadFile(file);
        xmlFileWriter = new XmlFileWriter(document, file);
        xmlWriter = new XmlTransformationWriter(document);
        xmlWriter.write(transformationSchema);
        xmlFileWriter.write();
    }

    private TransformationSchema read() {
        Document document = xmlFileReader.loadFile(file);
        return xmlReader.read(
            XMLUtil.getElement(document.getDocumentElement(), transformationElementName)
        );
    }
}
