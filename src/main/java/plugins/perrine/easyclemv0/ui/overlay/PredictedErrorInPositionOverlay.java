package plugins.perrine.easyclemv0.ui.overlay;

import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputer;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputerFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.workspace.Workspace;

import javax.inject.Inject;
import java.awt.*;

public class PredictedErrorInPositionOverlay extends Overlay {

    private Workspace workspace;
    private DatasetFactory datasetFactory;
    private TREComputerFactory treComputerFactory;

    @Inject
    public PredictedErrorInPositionOverlay(DatasetFactory datasetFactory, TREComputerFactory treComputerFactory) {
        super("Predicted Error from point configuration");
        this.datasetFactory = datasetFactory;
        this.treComputerFactory = treComputerFactory;
    }

    public PredictedErrorInPositionOverlay setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        return this;
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