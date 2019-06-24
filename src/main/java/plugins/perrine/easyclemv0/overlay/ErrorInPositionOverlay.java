package plugins.perrine.easyclemv0.overlay;

import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Workspace;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

public class ErrorInPositionOverlay extends Overlay {

    private Workspace workspace;
    private DatasetFactory datasetFactory = new DatasetFactory();

    public ErrorInPositionOverlay(Workspace workspace) {
        super("Difference in position");
        this.workspace = workspace;
    }

    @Override
    public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
        if ((canvas instanceof IcyCanvas2D) && (g != null)) {
            Dataset sourceDataset = datasetFactory.getFrom(workspace.getSourceSequence());
            Dataset targetDataset = datasetFactory.getFrom(workspace.getTargetSequence());
            g.setColor(Color.RED);
            for (int index = 0; index < sourceDataset.getN(); index++) {
                double error = sourceDataset.getPoint(index).getDistance(targetDataset.getPoint(index));

                if(sourceDataset.getDimension() == 2) {
                    g.setStroke(new BasicStroke((int) canvas.canvasToImageLogDeltaX(5)));
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    double l = error / 4;
                    double w = 3;

                    plotarrow(
                        sourceDataset.getPoint(index).getmatrix().get(0, 0),
                        sourceDataset.getPoint(index).getmatrix().get(1, 0),
                        targetDataset.getPoint(index).getmatrix().get(0, 0),
                        targetDataset.getPoint(index).getmatrix().get(1, 0),
                        l, w, g
                    );
                } else if(sourceDataset.getDimension() == 3) {
                    g.drawOval(
                        (int) Math.round(sourceDataset.getPoint(index).getmatrix().get(0, 0) - error),
                        (int) Math.round(sourceDataset.getPoint(index).getmatrix().get(1, 0) - error),
                        (int) Math.round(error * 2), (int) Math.round(error * 2)
                    );
                }
            }
        }
    }

    /**
     * Draw an arrow from point (x1,y1) to (x2,y2) with arrow-end length of l
     * and arrow-end width of w
     */
    private void plotarrow(double x1, double y1, double x2, double y2, double l, double w, Graphics2D g) {
        /*
         * c a ------------------- b d
         */
        double[] ab = { x2 - x1, y2 - y1 }; // ab vector
        double norm = Math.sqrt(ab[0] * ab[0] + ab[1] * ab[1]);
        if (norm > l) {// draw only if length(ab) > head length
            // t = ab vector normalized to l
            int[] t = { (int) Math.rint((double) ab[0] * (l / norm)), (int) Math.rint((double) ab[1] * (l / norm)) };

            double[] r = { ab[1], -ab[0] };
            norm = Math.sqrt(r[0] * r[0] + r[1] * r[1]);
            r[0] = (int) Math.rint((double) r[0] / norm * (w / 2));
            r[1] = (int) Math.rint((double) r[1] / norm * (w / 2));

            double[][] tri = { { x2, x2 - t[0] + r[0], x2 - t[0] - r[0], x2 },
                { y2, y2 - t[1] + r[1], y2 - t[1] - r[1], y2 } };
            Line2D l1 = new Line2D.Double(x1, y1, x2, y2);
            g.draw(l1);

            GeneralPath filledPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
            filledPolygon.moveTo(tri[0][0], tri[1][0]);
            for (int index = 1; index < 3; index++) {
                filledPolygon.lineTo(tri[0][index], tri[1][index]);
            }
            filledPolygon.closePath();
            g.fill(filledPolygon);
            g.draw(filledPolygon);
        }
    }
}
