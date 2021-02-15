package plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.reader;

import icy.util.XMLUtil;
import org.w3c.dom.Element;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.XmlTransformation;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;

import javax.inject.Inject;
import java.io.File;

import static plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.XmlTransformation.transformationElementName;

public class XmlToTransformationSchemaFileReader {

    private XmlFileReader xmlFileReader;
    private XmlTransformationReader xmlTransformationReader;

    @Inject
    public XmlToTransformationSchemaFileReader() {}

    public TransformationSchema read(File xmlFile) {
        return xmlTransformationReader.read(
            XMLUtil.getElement(xmlFileReader.loadFile(xmlFile).getDocumentElement(), XmlTransformation.transformationElementName)
        );
    }

    @Inject
    public void setXmlFileReader(XmlFileReader xmlFileReader) {
        this.xmlFileReader = xmlFileReader;
    }

    @Inject
    public void setXmlTransformationReader(XmlTransformationReader xmlTransformationReader) {
        this.xmlTransformationReader = xmlTransformationReader;
    }
}
