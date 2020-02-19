package plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.writer;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;
import java.io.File;

public class TransformationSchemaToXmlFileWriter {

    private XmlFileWriter xmlFileWriter;
    private XmlTransformationWriter xmlWriter;

    @Inject
    public TransformationSchemaToXmlFileWriter() {}

    public void save(TransformationSchema transformationSchema, File xmlFile) {
        Document document = XMLUtil.createDocument(true);
        xmlWriter.write(document, transformationSchema);
        xmlFileWriter.write(document, xmlFile);
    }

    @Inject
    public void setXmlFileWriter(XmlFileWriter xmlFileWriter) {
        this.xmlFileWriter = xmlFileWriter;
    }

    @Inject
    public void setXmlWriter(XmlTransformationWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
    }
}
