package plugins.perrine.easyclemv0.factory;

import Jama.Matrix;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.TransformationSchema;
import plugins.perrine.easyclemv0.model.transformation.Transformation;
import plugins.perrine.easyclemv0.roi.RoiProcessor;
import vtk.vtkPolyData;
import java.util.ArrayList;
import java.util.List;

public class DatasetFactory {

    private RoiProcessor roiProcessor = new RoiProcessor();
    private TransformationFactory transformationFactory = new TransformationFactory();

    public Dataset getFrom(Sequence sequence) {
        Dataset dataset;
        try {
            dataset = new Dataset(roiProcessor.getPointsFromRoi(sequence.getROIs()));
        } catch (Exception e) {
            dataset = new Dataset(0);
        }
        return toMicroMeter(dataset, sequence);
    }

    public Dataset getFrom(vtkPolyData points) {
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < points.GetNumberOfPoints(); i++) {
            double[] point = points.GetPoint(i);
            pointList.add(new Point(point));
        }
        return new Dataset(pointList);
    }

    public Dataset getFrom(Dataset dataset, TransformationSchema transformationSchema) {
        Transformation transformation = transformationFactory.getFrom(transformationSchema);
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
        return new Dataset(M);
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
        return new Dataset(M);
    }
}
