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
package plugins.perrine.easyclemv0.ec_clem.monitor;

import Jama.Matrix;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.main.Icy;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import icy.type.point.Point5D;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MonitorTargetOverlay extends Overlay {

    private MonitorTargetPoint monitorTargetPoint;
    private boolean notplacedyet = true;
    private Point point;

    public MonitorTargetOverlay(MonitorTargetPoint monitorTargetPoint) {
        super("Target point where to monitor accuracy");
        this.monitorTargetPoint = monitorTargetPoint;
        point = new Point(2);
    }

    @Override
    public void mouseMove(MouseEvent e, Point5D.Double imagePoint, IcyCanvas canvas) {
        if (notplacedyet) {
            if ((canvas instanceof IcyCanvas2D) && (imagePoint != null)) {
                point.getMatrix().set(0, 0, imagePoint.getX());
                point.getMatrix().set(1, 0, imagePoint.getY());
                Icy.getMainInterface().setSelectedTool("none");
                painterChanged();
            }
        }
    }

    @Override
    public void mouseClick(MouseEvent e, Point5D.Double imagePoint, IcyCanvas canvas) {
        if (notplacedyet) {
            if ((canvas instanceof IcyCanvas2D) && (imagePoint != null)) {
                notplacedyet = false;
                painterChanged();
                monitorTargetPoint.setMonitoringPoint(
                    new Point(new Matrix(new double[][] {
                        { imagePoint.getX() }, { imagePoint.getY() }, { imagePoint.getZ() }
                    }))
                );
            }
        }
    }

    @Override
    public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
        if ((canvas instanceof IcyCanvas2D) && (g != null)) {
            int xm = (int) point.getMatrix().get(0, 0);
            int ym = (int) point.getMatrix().get(1, 0);
            g.setColor(Color.GREEN);
            g.setStroke(new BasicStroke(3));
            int diameter=Math.round((float) sequence.getWidth() / 25);
            g.drawOval(xm-diameter/2, ym-diameter/2, Math.round(diameter), Math.round(diameter));
            g.setStroke(new BasicStroke(1));
            g.drawLine(xm-diameter/2 , ym -diameter/2, xm +diameter/2 , ym + diameter/2);
            g.drawLine(xm-diameter/2 , ym + diameter/2, xm + diameter/2, ym - diameter/2);
            g.drawLine(xm -diameter/2, ym -diameter/2, xm + diameter/2, ym + diameter/2);
            g.drawLine(xm-diameter/2, ym + diameter/2, xm + diameter/2, ym -diameter/2);
        }
    }
}
