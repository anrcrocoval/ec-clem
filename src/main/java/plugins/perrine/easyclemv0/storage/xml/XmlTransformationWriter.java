package plugins.perrine.easyclemv0.storage.xml;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.Transformation;

import java.time.DateTimeException;
import java.time.ZonedDateTime;

import static plugins.perrine.easyclemv0.storage.xml.XmlTransformation.*;

public class XmlTransformationWriter {

    private Document document;

    public XmlTransformationWriter(Document document) {
        this.document = document;
    }

    public void write(Transformation transformation) {
        Element transformationElement = XMLUtil.addElement(document.getDocumentElement(), transformationElementName);
        transformationElement.setAttribute(transformationTypeAttributeName, transformation.getTransformationType().name());
        transformationElement.setAttribute(transformationDateAttributeName, ZonedDateTime.now().toString());
        Element sourceDatasetElement = XMLUtil.addElement(transformationElement, datasetElementName);
        sourceDatasetElement.setAttribute(datasetTypeAttributeName, "source");
        write(transformation.getFiducialSet().getSourceDataset(), sourceDatasetElement);

        Element targetDatasetElement = XMLUtil.addElement(transformationElement, datasetElementName);
        sourceDatasetElement.setAttribute(datasetTypeAttributeName, "target");
        write(transformation.getFiducialSet().getTargetDataset(), targetDatasetElement);
    }

    private void write(Dataset dataset, Element datasetElement) {
        datasetElement.setAttribute(datasetNAttributeName, String.valueOf(dataset.getN()));
        datasetElement.setAttribute(datasetDimensionAttributeName, String.valueOf(dataset.getDimension()));
        for(int i = 0; i < dataset.getN(); i++) {
            Element point = XMLUtil.addElement(datasetElement, pointElementName);
            write(dataset.getPoint(i), point);
        }
    }

    private void write(Point point, Element pointElement) {
        for(int i = 0; i < point.getDimension(); i++) {
            Element coordinate = XMLUtil.addElement(pointElement, coordinateElementName);
            coordinate.setAttribute(coordinateDimensionAttributeName, String.valueOf(i));
            coordinate.setTextContent(String.valueOf(point.getmatrix().get(i, 0)));
        }
    }

}
