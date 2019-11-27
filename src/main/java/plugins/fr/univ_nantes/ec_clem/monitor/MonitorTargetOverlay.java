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
package plugins.fr.univ_nantes.ec_clem.monitor;

import Jama.Matrix;
import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.main.Icy;
import icy.painter.Overlay;
import icy.sequence.Sequence;
import icy.type.point.Point5D;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import java.awt.*;
import java.awt.event.MouseEvent;

public class MonitorTargetOverlay extends Overlay {

    private MonitoringConfiguration monitoringConfiguration;
    private boolean notplacedyet = true;

    public MonitorTargetOverlay(MonitoringConfiguration monitoringConfiguration) {
        super("Target point where to monitor accuracy");
        this.monitoringConfiguration = monitoringConfiguration;
    }

    @Override
    public void mouseMove(MouseEvent e, Point5D.Double imagePoint, IcyCanvas canvas) {
        if (notplacedyet) {
            if ((canvas instanceof IcyCanvas2D) && (imagePoint != null)) {
                monitoringConfiguration.setMonitoringPoint(new Point(new Matrix(new double[][] {
                    { imagePoint.getX() }, { imagePoint.getY() }
                })));
                Icy.getMainInterface().setSelectedTool("none");
                painterChanged();
            }
        }
    }

    @Override
    public void mouseClick(MouseEvent e, Point5D.Double imagePoint, IcyCanvas canvas) {
        if (notplacedyet) {
            if ((canvas instanceof IcyCanvas2D) && (imagePoint != null)) {
                monitoringConfiguration.setMonitoringPoint(new Point(new Matrix(new double[][] {
                    { imagePoint.getX() }, { imagePoint.getY() }, { imagePoint.getZ() }
                })));
                painterChanged();
                notplacedyet = false;
                monitoringConfiguration.setWaitfortarget(false);
                monitoringConfiguration.setMonitor(true);
//                if (canvas.getSequence().getName().equals(easyCLEMv0.target.getValue().getName())){
//                    easyCLEMv0.source.getValue().removeOverlay(myoverlaytarget);
//                }
//                if (canvas.getSequence().getName().equals(easyCLEMv0.source.getValue().getName())){
//                    easyCLEMv0.target.getValue().removeOverlay(myoverlaytarget);
//                    easyCLEMv0.monitortargetonsource = true;
//                }
//                canvas.getSequence().removeOverlay(this);
            }
        }
    }

    @Override
    public void paint(Graphics2D g, Sequence sequence, IcyCanvas canvas) {
        if ((canvas instanceof IcyCanvas2D) && (g != null)) {
            int xm = (int) monitoringConfiguration.getMonitoringPoint().getMatrix().get(0, 0);
            int ym = (int) monitoringConfiguration.getMonitoringPoint().getMatrix().get(1, 0);
            g.setColor(Color.GREEN);
            g.setStroke(new BasicStroke(3));
            int diameter=Math.round(sequence.getWidth()/25);
            g.drawOval(xm-diameter/2, ym-diameter/2, Math.round(diameter), Math.round(diameter));
            g.setStroke(new BasicStroke(1));
            g.drawLine(xm-diameter/2 , ym -diameter/2, xm +diameter/2 , ym + diameter/2);
            g.drawLine(xm-diameter/2 , ym + diameter/2, xm + diameter/2, ym - diameter/2);
            g.drawLine(xm -diameter/2, ym -diameter/2, xm + diameter/2, ym + diameter/2);
            g.drawLine(xm-diameter/2, ym + diameter/2, xm + diameter/2, ym -diameter/2);
//            if (!monitoringConfiguration.isWaitfortarget()) {
//                Icy.getMainInterface().setSelectedTool(ROI2DPointPlugin.class.getName());
//            }
        }
    }
}
