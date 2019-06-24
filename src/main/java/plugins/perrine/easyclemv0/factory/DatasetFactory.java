package plugins.perrine.easyclemv0.factory;

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
//        convertAllROI(sequence);
        return new Dataset(roiProcessor.getPointsFromRoi(sequence.getROIs()));
    }

    private void convertAllROI(Sequence sequence) {
        ArrayList<ROI> roiList = sequence.getROIs();
        roiProcessor.convert(roiList);
        sequence.removeAllROI();
        sequence.addROIs(roiList, false);
    }

    public Dataset getFrom(vtkPolyData points) {
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < points.GetNumberOfPoints(); i++) {
            double[] point = points.GetPoint(i);
            pointList.add(new Point(point));
        }
        return new Dataset(pointList);
    }
}
