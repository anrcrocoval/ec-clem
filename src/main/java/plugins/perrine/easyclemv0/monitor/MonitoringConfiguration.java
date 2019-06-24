package plugins.perrine.easyclemv0.monitor;

import plugins.perrine.easyclemv0.model.Point;

public class MonitoringConfiguration {

    private boolean monitor;
//    private boolean monitortargetonsource;
    private boolean waitfortarget;
    private Point monitoringPoint;

    public MonitoringConfiguration(boolean monitor, boolean waitfortarget) {
        this.monitor = monitor;
        this.waitfortarget = waitfortarget;
        monitoringPoint = new Point(new double[] {0, 0});
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
