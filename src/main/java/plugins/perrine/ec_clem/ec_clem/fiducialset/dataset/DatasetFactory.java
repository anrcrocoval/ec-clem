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
package plugins.perrine.ec_clem.ec_clem.fiducialset.dataset;

import Jama.Matrix;
import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.roi.RoiFactory;
import plugins.perrine.ec_clem.ec_clem.roi.RoiProcessor;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.roi.RoiFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.Transformation;
import plugins.perrine.ec_clem.ec_clem.transformation.RegistrationParameterFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import icy.sequence.Sequence;
import plugins.perrine.ec_clem.ec_clem.roi.RoiProcessor;
import vtk.vtkPolyData;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DatasetFactory {

    private RoiProcessor roiProcessor;
    private RoiFactory roiFactory;
    private RegistrationParameterFactory transformationFactory;

    @Inject
    public DatasetFactory(RoiProcessor roiProcessor, RoiFactory roiFactory, RegistrationParameterFactory transformationFactory) {
        this.roiProcessor = roiProcessor;
        this.roiFactory = roiFactory;
        this.transformationFactory = transformationFactory;
    }

    public Dataset getFrom(Sequence sequence) {
        Dataset dataset;
        try {
            dataset = new Dataset(
                roiProcessor.getPointsFromRoi(
                    roiFactory.getFrom(sequence)
                ),
                PointType.FIDUCIAL
            );
        } catch (RuntimeException e) {
            dataset = new Dataset(0, PointType.FIDUCIAL);
        }
        return toMicroMeter(dataset, sequence);
    }
    public Dataset getFrom(Sequence sequence, PointType pointType) {
        Dataset dataset;
        try {
            dataset = new Dataset(
                roiProcessor.getPointsFromRoi(
                    roiFactory.getFrom(sequence, pointType)
                ),
                pointType
            );
        } catch (RuntimeException e) {
            dataset = new Dataset(0, pointType);
        }
        return toMicroMeter(dataset, sequence);
    }

    public Dataset getFrom(vtkPolyData points, PointType pointType) {
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < points.GetNumberOfPoints(); i++) {
            double[] point = points.GetPoint(i);
            pointList.add(new Point(point));
        }
        return new Dataset(pointList, pointType);
    }

    public Dataset getFrom(Dataset dataset, TransformationSchema transformationSchema) {
        Transformation transformation = transformationFactory.getFrom(transformationSchema).getTransformation();
        Dataset transformedDataset;
        try {
            transformedDataset = transformation.apply(dataset);
        } catch (Exception e) {
            transformedDataset = dataset;
        }
        return transformedDataset;
    }

    public Dataset toPixel(Dataset dataset, Sequence sequence) {
        Matrix M = dataset.getMatrix().copy();
        for(int d = 0; d < dataset.getDimension(); d++) {
            if(d == 0) {
                M.setMatrix(0, dataset.getN() - 1, d, d, M.getMatrix(0, dataset.getN() - 1, d, d).times(1 / sequence.getPixelSizeX()));
            }
            if(d == 1) {
                M.setMatrix(0, dataset.getN() - 1, d, d, M.getMatrix(0, dataset.getN() - 1, d, d).times(1 /sequence.getPixelSizeY()));
            }
            if(d == 2) {
                M.setMatrix(0, dataset.getN() - 1, d, d, M.getMatrix(0, dataset.getN() - 1, d, d).times(1 / sequence.getPixelSizeZ()));
            }
        }
        return new Dataset(M, dataset.getPointType());
    }

    private Dataset toMicroMeter(Dataset dataset, Sequence sequence) {
        Matrix M = dataset.getMatrix().copy();
        
        for(int d = 0; d < dataset.getDimension(); d++) {
            if(d == 0) {
                M.setMatrix(0, dataset.getN() - 1, d, d, M.getMatrix(0, dataset.getN() - 1, d, d).times(sequence.getPixelSizeX()));
            }
            if(d == 1) {
                M.setMatrix(0, dataset.getN() - 1, d, d, M.getMatrix(0, dataset.getN() - 1, d, d).times(sequence.getPixelSizeY()));
            }
            if(d == 2) {
                M.setMatrix(0, dataset.getN() - 1, d, d, M.getMatrix(0, dataset.getN() - 1, d, d).times(sequence.getPixelSizeZ()));
            }
        }
        return new Dataset(M, dataset.getPointType());
    }
}
