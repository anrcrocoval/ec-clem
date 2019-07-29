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
package plugins.perrine.easyclemv0.storage;

import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.sequence.DimensionSize;
import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.schema.TransformationType;

import java.util.ArrayList;
import static plugins.perrine.easyclemv0.storage.XmlTransformation.*;

public class XmlTransformationReader {

    public TransformationSchema read(Element transformationElement) {
        TransformationType transformationType = TransformationType.valueOf(transformationElement.getAttribute(transformationTypeAttributeName));
        ArrayList<Element> datasetElements = XMLUtil.getElements(transformationElement, datasetElementName);
        if(datasetElements.size() != 2) {
            throw new RuntimeException("Element should contain exactly 2 dataset");
        }
        Dataset sourceDataset = readDataset(datasetElements.get(0));
        Dataset targetDataset = readDataset(datasetElements.get(1));
        FiducialSet fiducialSet;
        if(datasetElements.get(0).getAttribute(datasetTypeAttributeName).equals("source")) {
            fiducialSet =  new FiducialSet(sourceDataset, targetDataset);
        } else {
            fiducialSet =  new FiducialSet(targetDataset, sourceDataset);
        }

        ArrayList<Element> sequenceSizeElements = XMLUtil.getElements(transformationElement, sequenceSizeElementName);
        if(sequenceSizeElements.size() != 2) {
            throw new RuntimeException("Element should contain exactly 2 sequenceSize");
        }
        SequenceSize sourceSequenceSize = readSequenceSize(sequenceSizeElements.get(0));
        SequenceSize targetSequenceSize = readSequenceSize(sequenceSizeElements.get(1));

        if(sequenceSizeElements.get(0).getAttribute(sequenceSizeTypeAttributeName).equals("source")) {
            return new TransformationSchema(fiducialSet, transformationType, sourceSequenceSize, targetSequenceSize);
        } else {
            return new TransformationSchema(fiducialSet, transformationType, targetSequenceSize, sourceSequenceSize);
        }
    }

    private SequenceSize readSequenceSize(Element sequenceSizeElement) {
        SequenceSize sequenceSize = new SequenceSize();
        ArrayList<Element> elements = XMLUtil.getElements(sequenceSizeElement);
        for(Element dimension : elements) {
            sequenceSize.add(new DimensionSize(
                DimensionId.valueOf(dimension.getAttribute(dimensionSizeDimensionNameAttributeName)),
                Integer.valueOf(dimension.getTextContent()),
                Double.valueOf(dimension.getAttribute(dimensionSizePixelSizeAttributeName))
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
