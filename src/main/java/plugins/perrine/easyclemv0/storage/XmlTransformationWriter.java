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

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.sequence.DimensionSize;
import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;

import java.time.ZonedDateTime;

import static plugins.perrine.easyclemv0.storage.XmlTransformation.*;

public class XmlTransformationWriter {

    private Document document;

    public XmlTransformationWriter(Document document) {
        this.document = document;
    }

    public void write(TransformationSchema transformationSchema) {
        Element transformationElement = XMLUtil.addElement(document.getDocumentElement(), transformationElementName);
        transformationElement.setAttribute(transformationTypeAttributeName, transformationSchema.getTransformationType().name());
        transformationElement.setAttribute(transformationDateAttributeName, ZonedDateTime.now().toString());
        write(transformationSchema.getSourceSize(), "source", transformationElement);
        write(transformationSchema.getTargetSize(), "target", transformationElement);
        write(transformationSchema.getFiducialSet().getSourceDataset(), "source", transformationElement);
        write(transformationSchema.getFiducialSet().getTargetDataset(), "target", transformationElement);
    }

    private void write(SequenceSize sequenceSize, String type, Element transformationElement) {
        Element element = XMLUtil.addElement(transformationElement, sequenceSizeElementName);
        XMLUtil.setAttributeValue(element, sequenceSizeDimensionAttributeName, String.valueOf(sequenceSize.getN()));
        XMLUtil.setAttributeValue(element, sequenceSizeTypeAttributeName, type);
        for(DimensionSize entry : sequenceSize.getDimensions()) {
            Element value = XMLUtil.addElement(element, dimensionSizeElementName);
            value.setAttribute(dimensionSizeDimensionNameAttributeName, entry.getDimensionId().name());
            value.setAttribute(dimensionSizePixelSizeAttributeName, String.valueOf(entry.getPixelSizeInMicrometer()));
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
