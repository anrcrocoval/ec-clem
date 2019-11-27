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

import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;

public class MonitoringConfiguration {

    private boolean monitor;
//    private boolean monitortargetonsource;
    private boolean waitfortarget;
    private Point monitoringPoint;

    public MonitoringConfiguration(boolean monitor, boolean waitfortarget) {
        this.monitor = monitor;
        this.waitfortarget = waitfortarget;
        monitoringPoint = new Point(new double[] {0, 0, 0});
    }

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }

//    public boolean isMonitortargetonsource() {
//        return monitortargetonsource;
//    }
//
//    public void setMonitortargetonsource(boolean monitortargetonsource) {
//        this.monitortargetonsource = monitortargetonsource;
//    }

    public Point getMonitoringPoint() {
        return monitoringPoint;
    }

    public void setMonitoringPoint(Point monitoringPoint) {
        this.monitoringPoint = monitoringPoint;
    }

    public boolean isWaitfortarget() {
        return waitfortarget;
    }

    public void setWaitfortarget(boolean waitfortarget) {
        this.waitfortarget = waitfortarget;
    }
}
