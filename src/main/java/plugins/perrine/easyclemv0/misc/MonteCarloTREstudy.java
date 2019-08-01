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
//package plugins.perrine.easyclemv0;
///**
// * @author perrine.paul-gilloteaux@univ-nantes.fr
// * This plugin allow to compute the leave one out error for rigid
// * or non rig transformation based on monteCarlo Simulation to mimick the Fiducial localisation error
// * TODO implement the non rigid part and the 3D config.
// */
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Dimension;
//import javax.swing.JPanel;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.CategoryAxis;
//import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
//import org.jfree.chart.plot.CategoryPlot;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
//import org.jfree.chart.renderer.xy.DeviationRenderer;
//import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
//import org.jfree.data.xy.YIntervalSeries;
//import org.jfree.data.xy.YIntervalSeriesCollection;
//import org.jfree.ui.RectangleInsets;
//import icy.gui.frame.IcyFrame;
//import icy.gui.frame.progress.ProgressFrame;
//import icy.gui.frame.progress.ToolTipFrame;
//import icy.gui.util.GuiUtil;
//import plugins.adufour.ezplug.EzLabel;
//import plugins.adufour.ezplug.EzPlug;
//import plugins.adufour.ezplug.EzStoppable;
//import plugins.adufour.ezplug.EzVarDouble;
//import plugins.adufour.ezplug.EzVarInteger;
//import plugins.adufour.ezplug.EzVarSequence;
//import plugins.adufour.ezplug.EzVarText;
//import plugins.perrine.easyclemv0.error.TREChecker;
//import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
//import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputerFactory;
//import plugins.perrine.easyclemv0.model.*;
//import plugins.perrine.easyclemv0.transformation.Similarity;
//import SimilarityTransformationComputer;
//import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetGenerator;
//
//// mode 3D is not implemented here
//public class MonteCarloTREstudy extends EzPlug implements EzStoppable {
//
//	private EzVarSequence source;
//	private EzVarSequence target;
//	private EzVarDouble uFLE = new EzVarDouble("Fiducial localisation error in nm", 200,0,10000,10);
//	private EzVarInteger simulnumber = new EzVarInteger("Nb MonteCarlo Simulations",100, 10, 10000, 10);
//	EzVarText choiceinputsection = new EzVarText(
//		"I want to study the transformation in:",
//			new String[] {
//				"Rigid (or affine)",
//				"non Rigid)"
//			}, 0, false
//	);
//
//	private boolean stopflag;
//	private JPanel mainPanel = GuiUtil.generatePanel("Graph");
//	private IcyFrame mainFrame = GuiUtil.generateTitleFrame("Real configuration Error MC Simulations", mainPanel, new Dimension(300, 100), true, true, true, true);
//	private YIntervalSeries curve1;
//	private YIntervalSeries curve2;
//	private String namep;
//
//	private DatasetFactory datasetFactory = new DatasetFactory();
//	private SimilarityTransformationComputer rigidTransformationComputer = new SimilarityTransformationComputer();
//	private TREComputerFactory treComputerFactory = new TREComputerFactory();
//	private TREChecker treChecker = new TREChecker();
//	private DatasetGenerator datasetGenerator = new DatasetGenerator();
//
//	@Override
//	public void clean() {
//	}
//
//	@Override
//	protected void execute() {
//		mainPanel = GuiUtil.generatePanel("Graph");
//		mainFrame = GuiUtil.generateTitleFrame("Real configuration Error MC Simulations", mainPanel, new Dimension(300, 100), true, true, true, true);
//
//		stopflag = false;
//
//		Dataset sourceDataset = datasetFactory.getFrom(source.getValue());
//		Dataset targetDataset = datasetFactory.getFrom(target.getValue());
//
//		if (sourceDataset.getN() != targetDataset.getN()) {
//			System.out.println("source points different from target point");
//			return;
//		}
//
//		curve1 = new YIntervalSeries("Discrepancy");
//		curve2 = new YIntervalSeries("Predicted Error");
//
//		treChecker.check(sourceDataset, targetDataset);
//
//		double FLE = uFLE.getValue();
//		int nbsimul = simulnumber.getValue();
//
//		ProgressFrame myprogressbar = new ProgressFrame("Computing simulations...");
//
//		myprogressbar.setLength( nbsimul * sourceDataset.getN());
//		myprogressbar.setPosition(0);
//
//		final DefaultBoxAndWhiskerCategoryDataset dataset2 = new DefaultBoxAndWhiskerCategoryDataset();
//
//		for(int i = 0; i < sourceDataset.getN(); i++) {
//			if (stopflag) {
//				break;
//			}
//
//			RegistrationErrorStatistics registrationErrorStatistics = new RegistrationErrorStatistics();
//
//			for (int mc = 0; mc < nbsimul; mc++) {
//				myprogressbar.incPosition();
//
//				Dataset sourceDatasetWithNoise = datasetGenerator.addNoise(sourceDataset, FLE);
//				Dataset targetDatasetWithNoise = datasetGenerator.addNoise(targetDataset, FLE);
//
//				Point leaveOutSourcePoint = sourceDatasetWithNoise.removePoint(i);
//				Point leaveOutTargetPoint = targetDatasetWithNoise.removePoint(i);
//
//				Similarity similarity = rigidTransformationComputer.compute(sourceDatasetWithNoise, targetDatasetWithNoise);
//				Point transformedLeaveOutSourcePoint = similarity.apply(leaveOutSourcePoint);
//
//				RegistrationError registrationError = new RegistrationError(
//					treComputerFactory.getFrom(sourceDatasetWithNoise, targetDatasetWithNoise).getExpectedSquareTRE(leaveOutTargetPoint),
//					transformedLeaveOutSourcePoint.getDistance(leaveOutTargetPoint),
//					leaveOutTargetPoint.getDistance(targetDatasetWithNoise.getBarycentre())
//				);
//
//				registrationErrorStatistics.add(registrationError);
//			}
//
//			curve1.add(
//				registrationErrorStatistics.getDistanceToBarycentreStatistic().getAverage(),
//				registrationErrorStatistics.getMeasuredErrorStatistic().getAverage(),
//				registrationErrorStatistics.getMeasuredErrorStatistic().getMin(),
//				registrationErrorStatistics.getMeasuredErrorStatistic().getMax()
//			);
//
//			curve2.add(
//				registrationErrorStatistics.getDistanceToBarycentreStatistic().getAverage(),
//				registrationErrorStatistics.getPredictedErrorStatistic().getAverage(),
//				registrationErrorStatistics.getPredictedErrorStatistic().getMin(),
//				registrationErrorStatistics.getPredictedErrorStatistic().getMax()
//			);
//
//			dataset2.add(registrationErrorStatistics.getMeasuredError(), "Left one out discrepancy (Ground truth TRE)", "ROI " + namep);
//			dataset2.add(registrationErrorStatistics.getPredictedError(), "Predicted TRE ", "ROI " + namep);
//		}
//
//		final YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
//        dataset.addSeries(curve1);
//        dataset.addSeries(curve2);
//		// Step 6: write it/save it/ plot it
//        JFreeChart jfreechart=CreateChart(dataset,nbsimul,FLE);
//		ChartPanel  chartPanel = new ChartPanel(jfreechart);
//	    chartPanel.setFillZoomRectangle(true);
//	    chartPanel.setMouseWheelEnabled(true);
//	    chartPanel.setPreferredSize(new Dimension(500, 270));
//	    mainPanel.add(chartPanel);
//
//	    mainFrame.pack();
//
//	    addIcyFrame(mainFrame);
//
//	    mainFrame.setVisible(true);
//	    mainFrame.center();
//	    mainFrame.requestFocus();
//	    WhiskerPlot(dataset2);
//	    myprogressbar.close();
//	}
//
//	private void WhiskerPlot(DefaultBoxAndWhiskerCategoryDataset dataset2) {
//		final CategoryAxis xAxis = new CategoryAxis("Left Out Point");
//		final NumberAxis yAxis = new NumberAxis("in nanometers");
//		yAxis.setAutoRangeIncludesZero(true);
//		final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
//		renderer.setFillBox(true);
//		renderer.setMeanVisible(true);
//
//		renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
//		final CategoryPlot plot = new CategoryPlot(dataset2, xAxis, yAxis, renderer);
//		plot.setDomainGridlinesVisible(true);
//		plot.setRangePannable(true);
//		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
//		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//		final JFreeChart chart2 = new JFreeChart("Discrepancy distribution for TRE per ROI", JFreeChart.DEFAULT_TITLE_FONT, plot, true );
//		final ChartPanel chartPanel = new ChartPanel(chart2);
//		chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));
//		mainPanel.add(chartPanel);
//	}
//
//	private static JFreeChart CreateChart(YIntervalSeriesCollection dataset,int mc,double fle) {
//		 JFreeChart chart = ChartFactory.createXYLineChart(
//		            "Discrepancy vs error for "+mc+"  simulations, FLE= "+fle+ "nm",      // chart title
//		            "Distance from the center of gravity for the point removed ",                      // x axis label
//		            "in nm",                      // y axis label
//		            dataset,                  // data
//		            PlotOrientation.VERTICAL,
//		           true,                     // include legend
//		            true,                     // tooltips
//		            false                     // urls
//		 );
//        chart.setBackgroundPaint(Color.white);
//        XYPlot xyplot = (XYPlot) chart.getPlot();
//        xyplot.setInsets(new RectangleInsets(5D, 5D, 5D, 20D));
//        xyplot.setBackgroundPaint(Color.lightGray);
//        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
//        xyplot.setDomainGridlinePaint(Color.white);
//        xyplot.setRangeGridlinePaint(Color.white);
//        DeviationRenderer deviationrenderer = new DeviationRenderer(true, false);
//        deviationrenderer.setSeriesStroke(0, new BasicStroke(3F, 1, 1));
//        deviationrenderer.setSeriesShapesVisible(0, true);
//        deviationrenderer.setSeriesShapesVisible(1, true);
//        deviationrenderer.setSeriesStroke(1, new BasicStroke(3F, 1, 1));
//        deviationrenderer.setSeriesFillPaint(0, new Color(255, 200, 200));
//        deviationrenderer.setSeriesFillPaint(1, new Color(200, 200, 255));
//        xyplot.setRenderer(deviationrenderer);
//        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
//        numberaxis.setAutoRangeIncludesZero(true);
//        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        return chart;
//    }
//
//	@Override
//	protected void initialize() {
//		// TODO Auto-generated method stub
//		EzLabel textinfo1 = new EzLabel("Give information about error computation, usind leave one out as well.");
//		new ToolTipFrame(
//    			"<html>"+
//    			"<br>This plugin compute from a set of matching points: "+
//    			"<br> <li> The accuracy with Monte Carlo Simulations "+
//    			"<br>(moving randomly all points around their initial position with the FLE error),"+
//    			"<br>  of the registration error of a point against its target position" +
//    			"<br> when the point is left OUT the set of point for the registration (i.e N-1 points are used)</li>"+
//    			"<br><li> The predicted average  error on the same point, computed <b>without any ground truth</b></li> "+
//    			"<br><b> FLE </b> is the localization error you ca expect, i.e basically the resolution of your image "+
//    			"<br>(around 400 nm i Fluoresence for exemple), "+
//    			" <br>ROI Points should have similar names in both source and target image, such as Point 1, Point 2,..)"+
//    			"</html>"
//		);
//
//		source = new EzVarSequence("Select Source Image ");
//		target = new EzVarSequence("Select Target Image ");
//
//		addEzComponent(source);
//		addEzComponent(target);
//		addEzComponent(uFLE);
//		addEzComponent(simulnumber);
//		addEzComponent(textinfo1);
//		//addEzComponent(choiceinputsection);
//		new ToolTipFrame("Use a set of Roi as generated by Ec-Clem on 2 images.\n Pay attention to the image metadata (pixel size)");
//	}
//
//	@Override
//	public void stopExecution(){
//		stopflag=true;
//	}
//}
