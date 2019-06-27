package plugins.perrine.easyclemv0.storage.xml;

import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.model.*;
import java.util.ArrayList;
import static plugins.perrine.easyclemv0.storage.xml.XmlTransformation.*;

public class XmlTransformationReader {

    public Transformation read(Element transformationElement) {
        TransformationType transformationType = TransformationType.valueOf(transformationElement.getAttribute(transformationTypeAttributeName));
        ArrayList<Element> datasetElements = XMLUtil.getElements(transformationElement, datasetElementName);
        if(datasetElements.size() != 2) {
            throw new RuntimeException("Element should contain exactly 2 dataset");
        }
        Dataset dataset1 = readDataset(datasetElements.get(0));
        Dataset dataset2 = readDataset(datasetElements.get(1));
        FiducialSet fiducialSet;
        if(datasetElements.get(0).getAttribute(datasetTypeAttributeName).equals("source")) {
            fiducialSet =  new FiducialSet(dataset1, dataset2);
        } else {
            fiducialSet =  new FiducialSet(dataset2, dataset1);
        }

        ArrayList<Element> sequenceSizeElements = XMLUtil.getElements(transformationElement, imageSizeElementName);
        if(sequenceSizeElements.size() != 1) {
            throw new RuntimeException("Element should contain exactly 1 imageSize");
        }
        SequenceSize sequenceSize = readSequenceSize(sequenceSizeElements.get(0));
        return new Transformation(fiducialSet, transformationType, sequenceSize);
    }

    private SequenceSize readSequenceSize(Element sequenceSizeElement) {
        SequenceSize sequenceSize = new SequenceSize();
        ArrayList<Element> elements = XMLUtil.getElements(sequenceSizeElement);
        for(Element dimension : elements) {
            sequenceSize.add(new DimensionSize(
                DimensionId.valueOf(dimension.getAttribute(imageDimensionNameAttributeName)),
                Integer.valueOf(dimension.getTextContent()),
                Double.valueOf(dimension.getAttribute(dimensionpixelSizeAttributeName))
            ));
        }
        return sequenceSize;
    }

    private Dataset readDataset(Element datasetElement) {
        Dataset result = new Dataset(Integer.valueOf(datasetElement.getAttribute(datasetDimensionAttributeName)));
        ArrayList<Element> pointElements = XMLUtil.getElements(datasetElement);
        for(Element pointElement : pointElements) {
            result.addPoint(readPoint(pointElement));
        }
        return result;
    }

    private Point readPoint(Element pointElement) {
        ArrayList<Element> coordinateElements = XMLUtil.getElements(pointElement);
        Point result = new Point(coordinateElements.size());
        for(Element coordinate : coordinateElements) {
            result.getMatrix().set(Integer.valueOf(coordinate.getAttribute(coordinateDimensionAttributeName)), 0, Double.valueOf(coordinate.getTextContent()));
        }
        return result;
    }
}
