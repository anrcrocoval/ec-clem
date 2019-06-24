package plugins.perrine.easyclemv0.overlay;

import Jama.Matrix;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.painter.Overlay;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.type.point.Point5D;
import plugins.kernel.roi.descriptor.measure.ROIMassCenterDescriptorsPlugin;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.Workspace;

import java.awt.*;
import java.util.ArrayList;

public class PredictedErrorInPositionOverlay extends Overlay {

    private double FLEmax;
    private Workspace workspace;
    private DatasetFactory datasetFactory = new DatasetFactory();
    private TREComputerFactory treComputerFactory = new TREComputerFactory();

    public PredictedErrorInPositionOverlay(Workspace workspace) {
        super("Predicted Error from point configuration");
        this.workspace = workspace;
    }

    @Override
    public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
        if ((canvas instanceof IcyCanvas2D) && (g != null)) {
            TREComputer treComputer = treComputerFactory.getFrom(
                datasetFactory.getFrom(workspace.getSourceSequence()),
                datasetFactory.getFrom(workspace.getTargetSequence())
            );
            double xsource = sequence.getPixelSizeX();
            ArrayList<ROI> listfiducials = sequence.getROIs();
            for (ROI roi : listfiducials) {
                Point5D p3D = ROIMassCenterDescriptorsPlugin.computeMassCenter(roi);
                if (Double.isNaN(p3D.getX()))
                    p3D = roi.getPosition5D();
                int x = (int) Math.round(p3D.getX());
                int y = (int) Math.round(p3D.getY());
                g.setColor(Color.ORANGE);
                g.setStroke(new BasicStroke(5));
                double diameter = treComputer.getExpectedSquareTRE(new Point(new Matrix(new double[][] { { x }, { y }})));
                diameter = (diameter * 2) / (1000 * xsource);
                x = (int) Math.round(p3D.getX() - diameter / 2);
                y = (int) Math.round(p3D.getY() - diameter / 2);
                g.drawOval(x, y, (int) Math.round(diameter), (int) Math.round(diameter));
            }
        }
    }
}