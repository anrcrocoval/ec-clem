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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.sequence.DimensionSize;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceSize;
import plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.writer.TransformationSchemaToXmlFileWriter;
import plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;
import javax.inject.Inject;
import java.io.File;
import static org.testng.Assert.assertEquals;

class TransformationSchemaToXmlTest {
    private File file = new File(String.format("%s.test", getClass().getSimpleName()));

    private TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter;
    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;

    private SequenceSize sourceSequenceSize = new SequenceSize();
    private SequenceSize targetSequenceSize = new SequenceSize();

    public TransformationSchemaToXmlTest() {
        DaggerTransformationSchemaToXmlTestComponent.create().inject(this);
    }

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
            read.getFiducialSet().getSourceDataset().getMatrix().getColumnDimension()
        );
    }

    private void write(TransformationSchema transformationSchema) {
        transformationSchemaToXmlFileWriter.save(transformationSchema, file);
    }

    private TransformationSchema read() {
        return xmlToTransformationSchemaFileReader.read(file);
    }

    @Inject
    public void setTransformationSchemaToXmlFileWriter(TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter) {
        this.transformationSchemaToXmlFileWriter = transformationSchemaToXmlFileWriter;
    }

    @Inject
    public void setXmlToTransformationSchemaFileReader(XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader) {
        this.xmlToTransformationSchemaFileReader = xmlToTransformationSchemaFileReader;
    }
}
