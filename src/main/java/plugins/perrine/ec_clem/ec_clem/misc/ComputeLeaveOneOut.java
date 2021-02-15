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
//
//package plugins.perrine.ec_clem;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Dimension;
//import javax.swing.JPanel;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.ui.RectangleInsets;
//import icy.gui.frame.IcyFrame;
//import icy.gui.util.GuiUtil;
//import plugins.adufour.ezplug.EzLabel;
//import plugins.adufour.ezplug.EzPlug;
//import plugins.adufour.ezplug.EzVarSequence;
//import plugins.adufour.ezplug.EzVarText;
//import plugins.perrine.ec_clem.error.TREChecker;
//import plugins.perrine.ec_clem.fiducialset.dataset.DatasetFactory;
//import plugins.perrine.ec_clem.error.fitzpatrick.TREComputerFactory;
//import plugins.perrine.ec_clem.fiducialset.dataset.Dataset;
//import plugins.perrine.ec_clem.fiducialset.dataset.point.Point;
//import plugins.perrine.ec_clem.transformation.Similarity;
//import SimilarityTransformationComputer;
//
//// mode 3D is not implemented here
//public class ComputeLeaveOneOut extends EzPlug {
//
//	private EzVarSequence source;
//	private EzVarSequence target;
//	private EzVarText choiceinputsection = new EzVarText(
//		"I want to study the transformation in:",
//		new String[] {
//			"Rigid (or affine)",
//			"non Rigid)"
//		}, 0, false
//	);
//
//	private XYSeries curve = new XYSeries("TRE vs Discrepancy");
//	private JPanel mainPanel = GuiUtil.generatePanel("Graph");
//	private IcyFrame mainFrame = GuiUtil.generateTitleFrame("Target Registration Error (predicted)", mainPanel, new Dimension(300, 100), true, true, true, true);
//
//	private SimilarityTransformationComputer rigidTransformationComputer = new SimilarityTransformationComputer();
//	private DatasetFactory datasetFactory = new DatasetFactory();
//	private TREComputerFactory treComputerFactory = new TREComputerFactory();
//	private TREChecker treChecker = new TREChecker();
//
//	@Override
//	public void clean() {
//	}
//
//	@Override
//	protected void execute() {
//		Dataset sourceDataset = datasetFactory.getFrom(source.getValue());
//		Dataset targetDataset = datasetFactory.getFrom(target.getValue());
//
//		if (sourceDataset.getN() != targetDataset.getN()) {
//			System.out.println("source points different from target point");
//			return;
//		}
//
//		Similarity similarity = rigidTransformationComputer.compute(sourceDataset, targetDataset);
//		Dataset transformedSourceDataset = similarity.apply(sourceDataset);
//		treChecker.check(transformedSourceDataset, targetDataset);
//
//		for(int i = 0; i < sourceDataset.getN(); i++) {
//			Point leaveOutSourcePoint = sourceDataset.removePoint(i);
//			Point leaveOutTargetPoint = targetDataset.removePoint(i);
//
//			similarity = rigidTransformationComputer.compute(sourceDataset, targetDataset);
//			treChecker.check(sourceDataset, targetDataset);
//			Point transformedLeaveOutSourcePoint = similarity.apply(leaveOutSourcePoint);
//
//			treChecker.check(
//				new Dataset(transformedLeaveOutSourcePoint.getDimension()).addPoint(transformedLeaveOutSourcePoint),
//				new Dataset(leaveOutTargetPoint.getDimension()).addPoint(leaveOutTargetPoint)
//			);
//
//			curve.add(
//				treComputerFactory.getFrom(sourceDataset, targetDataset).getExpectedSquareTRE(leaveOutTargetPoint),
//				transformedLeaveOutSourcePoint.getDistance(leaveOutTargetPoint)
//			);
//
//			sourceDataset.addPoint(leaveOutSourcePoint);
//			targetDataset.addPoint(leaveOutTargetPoint);
//		}
//
//		final XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries(curve);
//		// Step 6: write it/save it/ plot it
//        JFreeChart jfreechart=CreateChart(dataset);
//		ChartPanel  chartPanel = new ChartPanel(jfreechart);
//	    chartPanel.setFillZoomRectangle(true);
//	    chartPanel.setMouseWheelEnabled(true);
//	    chartPanel.setPreferredSize(new Dimension(500, 270));
//	    mainPanel.add(chartPanel);
//	    mainFrame.pack();
//	    addIcyFrame(mainFrame);
//	    mainFrame.setVisible(true);
//	    mainFrame.center();
//	    mainFrame.requestFocus();
//	}
//
//	private static JFreeChart CreateChart(XYSeriesCollection dataset) {
//		 JFreeChart chart = ChartFactory.createXYLineChart(
//		 	"TRE vs Discrepancy ",
//			 "TRE in nm",
//			 "Discrepancy in nm",
//			 dataset,
//			 PlotOrientation.VERTICAL,
//			 false,
//			 true,
//			 false
//		 );
//		 chart.setBackgroundPaint(Color.white);
//		 XYPlot xyplot = (XYPlot) chart.getPlot();
//		 xyplot.setInsets(new RectangleInsets(5D, 5D, 5D, 20D));
//		 xyplot.setBackgroundPaint(Color.lightGray);
//		 xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
//		 xyplot.setDomainGridlinePaint(Color.white);
//		 xyplot.setRangeGridlinePaint(Color.white);
//		 XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
//		 renderer.setSeriesPaint( 0 , Color.RED );
//		 renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f ) );
//		 renderer.setSeriesLinesVisible(0, false);
//		 xyplot.setRenderer(renderer);
//		 NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
//
//		 numberaxis.setAutoRangeIncludesZero(true);
//		 numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//		 return chart;
//	}
//
//	@Override
//	protected void initialize() {
//		EzLabel textinfo1 = new EzLabel("Give information about error computation, usind leave one out as well.");
//		source = new EzVarSequence("Select Source Image ");
//		target = new EzVarSequence("Select Target Image ");
//		addEzComponent(source);
//		addEzComponent(target);
//		addEzComponent(textinfo1);
//		addEzComponent(choiceinputsection);
//	}
//}
