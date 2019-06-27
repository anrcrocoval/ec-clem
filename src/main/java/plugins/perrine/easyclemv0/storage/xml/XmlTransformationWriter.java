package plugins.perrine.easyclemv0.storage.xml;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.model.*;

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
        write(transformation.getTargetSize(), transformationElement);
        write(transformation.getFiducialSet().getSourceDataset(), "source", transformationElement);
        write(transformation.getFiducialSet().getTargetDataset(), "target", transformationElement);
    }

    private void write(SequenceSize sequenceSize, Element transformationElement) {
        Element element = XMLUtil.addElement(transformationElement, imageSizeElementName);
        XMLUtil.setAttributeValue(element, imageDimensionAttributeName, String.valueOf(sequenceSize.getN()));
        for(DimensionSize entry : sequenceSize.getDimensions()) {
            Element value = XMLUtil.addElement(element, dimensionSizeElementName);
            value.setAttribute(imageDimensionNameAttributeName, entry.getDimensionId().name());
            value.setAttribute(dimensionpixelSizeAttributeName, String.valueOf(entry.getPixelSizeInNanometer()));
            value.setTextContent(String.valueOf(entry.getSize()));
            element.appendChild(value);
        }
    }

    private void write(Dataset dataset, String type, Element transformationElement) {
        Element sourceDatasetElement = XMLUtil.addElement(transformationElement, datasetElementName);
        sourceDatasetElement.setAttribute(datasetTypeAttributeName, type);
        write(dataset, sourceDatasetElement);
    }

    private void write(Dataset dataset, Element datasetElement) {
        datasetElement.setAttribute(datasetNAttributeName, String.valueOf(dataset.getN()));
        datasetElement.setAttribute(datasetDimensionAttributeName, String.valueOf(dataset.getDimension()));
        for(int i = 0; i < dataset.getN(); i++) {
            Element point = XMLUtil.addElement(datasetElement, pointElementName);
            point.setAttribute(pointIdAttributeName, String.valueOf(i));
            write(dataset.getPoint(i), point);
        }
    }

    private void write(Point point, Element pointElement) {
        for(int i = 0; i < point.getDimension(); i++) {
            Element coordinate = XMLUtil.addElement(pointElement, coordinateElementName);
            coordinate.setAttribute(coordinateDimensionAttributeName, String.valueOf(i));
            coordinate.setTextContent(String.valueOf(point.getMatrix().get(i, 0)));
        }
    }
}
