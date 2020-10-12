package plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation.xml;

import Jama.Matrix;
import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence.SequenceSize;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation_schema.writer.XmlTransformationWriter;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.SplineTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Transformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationSchema;

import javax.inject.Inject;
import java.io.File;
import java.util.Date;

public class TransformationToXmlFileWriter {

    @Inject
    public TransformationToXmlFileWriter() {}

    public void save(Transformation transformation, TransformationSchema transformationSchema, File xmlFile) {
        if(transformation instanceof AffineTransformation) {
            format((AffineTransformation) transformation, transformationSchema, xmlFile);
        } else if(transformation instanceof SplineTransformation) {
            throw new RuntimeException("Not implemented");
        } else {
            throw new RuntimeException("Missing binding");
        }
    }

    private void format(AffineTransformation transformation, TransformationSchema transformationSchema, File xmlFile) {
        Document document = XMLUtil.createDocument(true);
       
        writeMatrix(document, transformation.getHomogeneousMatrix());
        XMLUtil.saveDocument(document, xmlFile);
        XmlTransformationWriter writer = new XmlTransformationWriter();
        writer.writeSequenceInfoOnly(document, transformationSchema);
    }

   

    private void writeMatrix(Document document, Matrix matrix) {
        Element element = XMLUtil.addElement(document.getDocumentElement(), "MatrixTransformation");
        for(int i = 0; i < matrix.getRowDimension(); i++) {
            for(int j = 0; j < matrix.getColumnDimension(); j++) {
                XMLUtil.setAttributeDoubleValue(element, String.format("m%d%d", i, j), matrix.get(i, j));
            }
        }
       
        XMLUtil.setAttributeValue(element, "process_date", new Date().toString());
    }
}
