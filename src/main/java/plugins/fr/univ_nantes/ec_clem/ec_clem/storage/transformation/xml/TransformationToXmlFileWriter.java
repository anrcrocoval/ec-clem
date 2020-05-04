package plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation.xml;

import Jama.Matrix;
import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence.SequenceSize;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.SplineTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Transformation;
import javax.inject.Inject;
import java.io.File;
import java.util.Date;

public class TransformationToXmlFileWriter {

    @Inject
    public TransformationToXmlFileWriter() {}

    public void save(Transformation transformation, SequenceSize sequenceSize, int nFiducial, File xmlFile) {
        if(transformation instanceof AffineTransformation) {
            format((AffineTransformation) transformation, sequenceSize, nFiducial, xmlFile);
        } else if(transformation instanceof SplineTransformation) {
            throw new RuntimeException("Not implemented");
        } else {
            throw new RuntimeException("Missing binding");
        }
    }

    private void format(AffineTransformation transformation, SequenceSize sequenceSize, int nFiducial, File xmlFile) {
        Document document = XMLUtil.createDocument(true);
        writeTargetSize(document, sequenceSize);
        writeMatrix(document, transformation.getHomogeneousMatrix(), nFiducial);
        XMLUtil.saveDocument(document, xmlFile);
    }

    private void writeTargetSize(Document document, SequenceSize sequenceSize) {
        Element element = XMLUtil.addElement(document.getDocumentElement(), "TargetSize");
        XMLUtil.setAttributeIntValue(element, "width", sequenceSize.get(DimensionId.X).getSize());
        XMLUtil.setAttributeIntValue(element, "height", sequenceSize.get(DimensionId.Y).getSize());
        XMLUtil.setAttributeIntValue(element, "nz", sequenceSize.get(DimensionId.Z).getSize());
        XMLUtil.setAttributeDoubleValue(element, "sx", sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer());
        XMLUtil.setAttributeDoubleValue(element, "sy", sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer());
        XMLUtil.setAttributeDoubleValue(element, "sz", sequenceSize.get(DimensionId.Z).getPixelSizeInMicrometer());
    }

    private void writeMatrix(Document document, Matrix matrix, int nFiducial) {
        Element element = XMLUtil.addElement(document.getDocumentElement(), "MatrixTransformation");
        for(int i = 0; i < matrix.getRowDimension(); i++) {
            for(int j = 0; j < matrix.getColumnDimension(); j++) {
                XMLUtil.setAttributeDoubleValue(element, String.format("m%d%d", i, j), matrix.get(i, j));
            }
        }
        XMLUtil.setAttributeIntValue(element, "order", nFiducial);
        XMLUtil.setAttributeValue(element, "process_date", new Date().toString());
    }
}
