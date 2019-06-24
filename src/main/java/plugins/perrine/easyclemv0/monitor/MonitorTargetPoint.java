/**
 * Copyright 2010-2017 Perrine Paul-Gilloteaux, CNRS.
 * Perrine.Paul-Gilloteaux@univ-nantes.fr
 * 
 * This file is part of EC-CLEM.
 * 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 **/


package plugins.perrine.easyclemv0.monitor;


import icy.gui.frame.IcyFrame;
import icy.gui.util.GuiUtil;
import icy.plugin.abstract_.PluginActionable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.ui.RectangleInsets;


public class MonitorTargetPoint extends PluginActionable {

    private static JFreeChart jfreechart;
    private static XYSeries yintervalseries;
	private JPanel mainPanel = GuiUtil.generatePanel("Graph");
    private IcyFrame mainFrame = GuiUtil.generateTitleFrame("Target Registration Error (predicted)", mainPanel, new Dimension(300, 100), true, true, true, true);
	private int N;

    @Override
    public void run() {
        N=0;
        XYDataset dataset = createDataset(N);
        createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(jfreechart);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        mainPanel.add(chartPanel);
        mainFrame.pack();
        addIcyFrame(mainFrame);
        mainFrame.setVisible(true);
        mainFrame.center();
        mainFrame.requestFocus();
    }

    private static XYDataset createDataset(int N) {
        yintervalseries = new XYSeries("TRE in nm");
        for (int i = 1; i <= N; i++) {
            yintervalseries.add(i, i);
        }
        XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries(yintervalseries);
        return dataset;
    }
    
    private void createChart(XYDataset xydataset) {
        jfreechart = ChartFactory.createXYLineChart("Predicted Error on monitored target", "Number of points used for the registration", "TRE", xydataset, PlotOrientation.VERTICAL, true, false, false);
        jfreechart.setBackgroundPaint(Color.white);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setInsets(new RectangleInsets(5D, 5D, 5D, 20D));
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesPaint( 0 , Color.RED );
        renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f ) );
        xyplot.setRenderer(renderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }

    public static void UpdatePoint(double[][] newvalues) {
        yintervalseries.clear();
        for (double[] newvalue : newvalues) {
            yintervalseries.add(newvalue[0], newvalue[1]);
            XYPlot xyplot = (XYPlot) jfreechart.getPlot();
            NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
            numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            numberaxis.setAutoRangeIncludesZero(false);
        }
    }
}
