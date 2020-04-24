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
package plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.reader;

import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.w3c.dom.Element;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.sequence.DimensionSize;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceSize;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;

import javax.inject.Inject;
import java.util.ArrayList;
import static plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.XmlTransformation.*;

public class XmlTransformationReader {

    @Inject
    public XmlTransformationReader() {}

    public TransformationSchema read(Element transformationElement) {
        TransformationType transformationType = TransformationType.valueOf(transformationElement.getAttribute(transformationTypeAttributeName));
        NoiseModel noiseModel = NoiseModel.valueOf(transformationElement.getAttribute(transformationNoiseModelAttributeName));
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
            return new TransformationSchema(fiducialSet, transformationType, noiseModel, sourceSequenceSize, targetSequenceSize);
        } else {
            return new TransformationSchema(fiducialSet, transformationType, noiseModel, targetSequenceSize, sourceSequenceSize);
        }
    }

    private SequenceSize readSequenceSize(Element sequenceSizeElement) {
        SequenceSize sequenceSize = new SequenceSize();
        ArrayList<Element> elements = XMLUtil.getElements(sequenceSizeElement);
        for(Element dimension : elements) {
            sequenceSize.add(new DimensionSize(
                DimensionId.valueOf(dimension.getAttribute(dimensionSizeDimensionNameAttributeName)),
                Integer.parseInt(dimension.getTextContent()),
                Double.parseDouble(dimension.getAttribute(dimensionSizePixelSizeAttributeName))
            ));
        }
        return sequenceSize;
    }

    private Dataset readDataset(Element datasetElement) {
        Dataset result = new Dataset(
            Integer.parseInt(datasetElement.getAttribute(datasetDimensionAttributeName)),
            PointType.valueOf(datasetElement.getAttribute(datasetPointTypeAttributeName))
        );
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
            result.getMatrix().set(Integer.parseInt(coordinate.getAttribute(coordinateDimensionAttributeName)), 0, Double.parseDouble(coordinate.getTextContent()));
        }
        return result;
    }
}
