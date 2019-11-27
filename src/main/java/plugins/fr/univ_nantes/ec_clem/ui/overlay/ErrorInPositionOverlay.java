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
package plugins.fr.univ_nantes.ec_clem.ui.overlay;

import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSetFactory;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.workspace.Workspace;
import javax.inject.Inject;
import java.awt.*;

public class ErrorInPositionOverlay extends Overlay {

    private Workspace workspace;
    private FiducialSetFactory fiducialSetFactory;

    @Inject
    public ErrorInPositionOverlay(FiducialSetFactory fiducialSetFactory) {
        super("Difference in position");
        this.fiducialSetFactory = fiducialSetFactory;
    }

    public ErrorInPositionOverlay setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        return this;
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
