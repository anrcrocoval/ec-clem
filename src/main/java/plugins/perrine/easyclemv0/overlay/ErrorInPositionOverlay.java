package plugins.perrine.easyclemv0.overlay;

import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.FiducialSetFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.Workspace;
import java.awt.*;

public class ErrorInPositionOverlay extends Overlay {

    private Workspace workspace;
    private FiducialSetFactory fiducialSetFactory = new FiducialSetFactory();

    public ErrorInPositionOverlay(Workspace workspace) {
        super("Difference in position");
        this.workspace = workspace;
    }

    @Override
    public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
        if ((canvas instanceof IcyCanvas2D) && (g != null)) {
            if(workspace.getTransformationSchema() != null) {
                FiducialSet fiducialSet = fiducialSetFactory.getFrom(workspace);
                Dataset sourceDataset = fiducialSet.getSourceDataset();
                Dataset targetDataset = fiducialSet.getTargetDataset();
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(1));
                for (int index = 0; index < sourceDataset.getN(); index++) {
                    double error = sourceDataset.getPoint(index).getDistance(targetDataset.getPoint(index));
                    g.drawOval(
                        (int) Math.round((sourceDataset.getPoint(index).getMatrix().get(0, 0) - error) / sequence.getPixelSizeX()),
                        (int) Math.round((sourceDataset.getPoint(index).getMatrix().get(1, 0) - error) / sequence.getPixelSizeY()),
                        (int) Math.round(error / sequence.getPixelSizeX() * 2),
                        (int) Math.round(error / sequence.getPixelSizeY() * 2)
                    );
                }
            }
        }
    }
}
