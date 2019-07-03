package plugins.perrine.easyclemv0.factory;

import Jama.Matrix;
import icy.roi.ROI;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.roi.RoiProcessor;
import vtk.vtkPolyData;

import java.util.ArrayList;
import java.util.List;

public class DatasetFactory {

    private RoiProcessor roiProcessor = new RoiProcessor();

    public Dataset getFrom(Sequence sequence) {
        return toMicroMeter(new Dataset(roiProcessor.getPointsFromRoi(sequence.getROIs())), sequence);
    }

    public Dataset getFrom(vtkPolyData points) {
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < points.GetNumberOfPoints(); i++) {
            double[] point = points.GetPoint(i);
            pointList.add(new Point(point));
        }
        return new Dataset(pointList);
    }

    public Dataset toMicroMeter(Dataset dataset, Sequence sequence) {
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

    private void convertAllROI(Sequence sequence) {
        ArrayList<ROI> roiList = sequence.getROIs();
        roiProcessor.convert(roiList);
        sequence.removeAllROI();
        sequence.addROIs(roiList, false);
    }
}
