package plugins.perrine.easyclemv0.overlay;

import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Workspace;
import java.awt.*;

public class PredictedErrorInPositionOverlay extends Overlay {

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
            if(workspace.getTransformationSchema() != null) {
                TREComputer treComputer = treComputerFactory.getFrom(
                        workspace.getTransformationSchema()
                );

                g.setColor(Color.ORANGE);
                g.setStroke(new BasicStroke(1));
                Dataset sourceDataset = datasetFactory.getFrom(workspace.getSourceSequence());
                for (int index = 0; index < sourceDataset.getN(); index++) {
                    double error = treComputer.getExpectedSquareTRE(sourceDataset.getPoint(index));
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