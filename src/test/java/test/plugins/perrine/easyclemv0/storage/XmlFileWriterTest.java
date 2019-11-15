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
package test.plugins.perrine.easyclemv0.storage;

import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.sequence.DimensionSize;
import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.perrine.easyclemv0.storage.XmlFileReader;
import plugins.perrine.easyclemv0.storage.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.XmlTransformationWriter;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.schema.TransformationType;
import plugins.perrine.easyclemv0.storage.XmlTransformationReader;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static plugins.perrine.easyclemv0.storage.XmlTransformation.transformationElementName;

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
        Dataset sourceDataset = new Dataset(new double[][] {{ 1, 2, 0 }, { 2, 3, 0}, { 3, 4, 1 }});
        Dataset targetDataset = new Dataset(new double[][] {{ 1, 2, 0 }, { 2, 3, 0}, { 3, 4, 1 }});
        sourceSequenceSize.add(new DimensionSize(DimensionId.X, 10, 1));
        targetSequenceSize.add(new DimensionSize(DimensionId.X, 10, 1));
        TransformationSchema transformationSchema = new TransformationSchema(
            new FiducialSet(sourceDataset, targetDataset),
            TransformationType.SIMILARITY,
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
