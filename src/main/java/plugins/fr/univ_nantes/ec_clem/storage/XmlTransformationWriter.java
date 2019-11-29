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
package plugins.fr.univ_nantes.ec_clem.storage;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.sequence.DimensionSize;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceSize;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;

import java.time.ZonedDateTime;

public class XmlTransformationWriter {

    private Document document;

    public XmlTransformationWriter(Document document) {
        this.document = document;
    }

    public void write(TransformationSchema transformationSchema) {
        Element transformationElement = XMLUtil.addElement(document.getDocumentElement(), XmlTransformation.transformationElementName);
        transformationElement.setAttribute(XmlTransformation.transformationTypeAttributeName, transformationSchema.getTransformationType().name());
        transformationElement.setAttribute(XmlTransformation.transformationDateAttributeName, ZonedDateTime.now().toString());
        write(transformationSchema.getSourceSize(), "source", transformationElement);
        write(transformationSchema.getTargetSize(), "target", transformationElement);
        write(transformationSchema.getFiducialSet().getSourceDataset(), "source", transformationElement);
        write(transformationSchema.getFiducialSet().getTargetDataset(), "target", transformationElement);
    }

    private void write(SequenceSize sequenceSize, String type, Element transformationElement) {
        Element element = XMLUtil.addElement(transformationElement, XmlTransformation.sequenceSizeElementName);
        XMLUtil.setAttributeValue(element, XmlTransformation.sequenceSizeDimensionAttributeName, String.valueOf(sequenceSize.getN()));
        XMLUtil.setAttributeValue(element, XmlTransformation.sequenceSizeTypeAttributeName, type);
        for(DimensionSize entry : sequenceSize.getDimensions()) {
            Element value = XMLUtil.addElement(element, XmlTransformation.dimensionSizeElementName);
            value.setAttribute(XmlTransformation.dimensionSizeDimensionNameAttributeName, entry.getDimensionId().name());
            value.setAttribute(XmlTransformation.dimensionSizePixelSizeAttributeName, String.valueOf(entry.getPixelSizeInMicrometer()));
            value.setTextContent(String.valueOf(entry.getSize()));
            element.appendChild(value);
        }
    }

    private void write(Dataset dataset, String type, Element transformationElement) {
        Element sourceDatasetElement = XMLUtil.addElement(transformationElement, XmlTransformation.datasetElementName);
        sourceDatasetElement.setAttribute(XmlTransformation.datasetTypeAttributeName, type);
        write(dataset, sourceDatasetElement);
    }

    private void write(Dataset dataset, Element datasetElement) {
        datasetElement.setAttribute(XmlTransformation.datasetNAttributeName, String.valueOf(dataset.getN()));
        datasetElement.setAttribute(XmlTransformation.datasetDimensionAttributeName, String.valueOf(dataset.getDimension()));
        datasetElement.setAttribute(XmlTransformation.datasetPointTypeAttributeName, dataset.getPointType().name());
        for(int i = 0; i < dataset.getN(); i++) {
            Element point = XMLUtil.addElement(datasetElement, XmlTransformation.pointElementName);
            point.setAttribute(XmlTransformation.pointIdAttributeName, String.valueOf(i));
            write(dataset.getPoint(i), point);
        }
    }

    private void write(Point point, Element pointElement) {
        for(int i = 0; i < point.getDimension(); i++) {
            Element coordinate = XMLUtil.addElement(pointElement, XmlTransformation.coordinateElementName);
            coordinate.setAttribute(XmlTransformation.coordinateDimensionAttributeName, String.valueOf(i));
            coordinate.setTextContent(String.valueOf(point.getMatrix().get(i, 0)));
        }
    }
}
