package plugins.perrine.easyclemv0.storage;

import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import plugins.perrine.easyclemv0.factory.FiducialSetFactory;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.storage.xml.XmlFileReader;
import plugins.perrine.easyclemv0.storage.xml.XmlFileWriter;
import plugins.perrine.easyclemv0.storage.xml.XmlTransformationReader;
import plugins.perrine.easyclemv0.storage.xml.XmlTransformationWriter;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static plugins.perrine.easyclemv0.storage.xml.XmlTransformation.transformationElementName;

class XmlFileWriterTest {
    private FiducialSetFactory fiducialSetFactory = new FiducialSetFactory();
    private File file = new File("XmlFileWriterTest.xml");
    private XmlFileReader xmlFileReader = new XmlFileReader();
    private XmlFileWriter xmlFileWriter;
    private XmlTransformationReader xmlReader = new XmlTransformationReader();
    private XmlTransformationWriter xmlWriter;
    private SequenceSize sequenceSize = new SequenceSize();

    @BeforeEach
    @AfterEach
    private void clearFile() {
        file.delete();
    }

    @Test
    void writeAndRead() {
        Dataset sourceDataset = new Dataset(new double[][] {{ 1, 2, 0 }, { 2, 3, 0}, { 3, 4, 1 }});
        Dataset targetDataset = new Dataset(new double[][] {{ 1, 2, 0 }, { 2, 3, 0}, { 3, 4, 1 }});
        sequenceSize.add(new DimensionSize(DimensionId.X, 10, 1));
        Transformation transformation = new Transformation(
            new FiducialSet(sourceDataset, targetDataset),
            TransformationType.RIGID,
            sequenceSize
        );
        write(transformation);
        Transformation read = read();
        assertEquals(transformation.getTransformationType(), read.getTransformationType());
        assertEquals(
            transformation.getFiducialSet().getSourceDataset().getMatrix().getColumnDimension(),
            read.getFiducialSet().getSourceDataset().getMatrix().getColumnDimension());
    }

    private void write(Transformation transformation) {
        Document document = xmlFileReader.loadFile(file);
        xmlFileWriter = new XmlFileWriter(document, file);
        xmlWriter = new XmlTransformationWriter(document);
        xmlWriter.write(transformation);
        xmlFileWriter.write();
    }

    private Transformation read() {
        Document document = xmlFileReader.loadFile(file);
        return xmlReader.read(
            XMLUtil.getElement(document.getDocumentElement(), transformationElementName)
        );
    }
}
