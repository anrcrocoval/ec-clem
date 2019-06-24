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


package plugins.perrine.easyclemv0;
/**
 * @author perrine.paul-gilloteaux@univ-nantes.fr
 * This plugin allow to compute the leave one out error for rigid 
 * or non rig transformation based on monteCarlo Simulation to mimick the Fiducial localisation error
 * TODO implement  the 3D config. started
 * Added the comparison with thin plate spline transform (leave one out discrepancy)
 * TODO toujours le bug sur alpha: corriger roi2D en 3D des le d�but...
 *  
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.DeviationRenderer;

import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.RectangleInsets;


import icy.gui.frame.IcyFrame;
import icy.gui.frame.progress.ProgressFrame;
import icy.gui.frame.progress.ToolTipFrame;
import icy.gui.util.GuiUtil;
import icy.roi.ROI;

import icy.sequence.Sequence;

import icy.type.point.Point5D;

import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzStoppable;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.adufour.ezplug.EzVarInteger;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.ezplug.EzVarText;
import plugins.perrine.easyclemv0.error.TREChecker;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.registration.RigidTransformationComputer;
import plugins.perrine.easyclemv0.util.DatasetGenerator;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkThinPlateSplineTransform;
import vtk.vtkTransformPolyDataFilter;
import vtk.vtkVertexGlyphFilter;


// mode 3D is not implemented here
public class MonteCarloTREStudy_Validation extends EzPlug implements EzStoppable {

	private EzVarSequence source;
	private EzVarBoolean withFLE=new EzVarBoolean("Including target localisation error",false);
	private EzVarDouble uFLE=new EzVarDouble("Fiducial localisation error in nm", 200,0,10000000,10);
	private EzVarInteger simulnumber = new EzVarInteger("Nb MonteCarlo Simulations",100, 10, 10000, 10);
	private EzVarSequence target;
	private EzVarText choiceinputsection = new EzVarText("I want to study the transformation in:",
			new String[] { "Rigid and prediction","compare with non Rigid"}, 0, false);

	private boolean mode3D=false;

	boolean stopflag;
	private Random generator= new Random();
	private JPanel mainPanel;
	private IcyFrame mainFrame ;
	private YIntervalSeries curve1;
	private YIntervalSeries curve2;
	private YIntervalSeries curve3;
	private String namep;

	private Sequence sourceseq;

	private FileWriter write;

	private DatasetFactory datasetFactory = new DatasetFactory();
	private RigidTransformationComputer rigidTransformationComputer = new RigidTransformationComputer();
	private TREChecker treChecker = new TREChecker();
	private DatasetGenerator datasetGenerator = new DatasetGenerator();
	private TREComputerFactory treComputerFactory = new TREComputerFactory();

	@Override
	public void clean() {
	}

	/**
	 *
	 * @param points in pixels
	 * @param sizex in um
	 * @param sizey in um
	 * @param sizez ignored for now
	 * @return vtk points in um
	 */
	private vtkPoints createvtkpoints(double[][] points,double sizex,double sizey,double sizez) {
		// points in pixels
		vtkPoints mypoints=new vtkPoints();
		mypoints.SetNumberOfPoints(points.length);
		 for (int i=0;i<points.length;i++){
			 mypoints.SetPoint(i,points[i][0]*sizex, points[i][1]*sizey, 0.0);
		 }

		return mypoints;

	}
	//2D only for now
	/**
	 * 
	 * @param lmsourcepts in pixels
	 * @param lmtargetpts in pixels
	 * @param test in pixels
	 * @param targetpoint in pixels
	 * @return distance after non rigid transform (oit out) in nm
	 */
	private double computenonrigid(double[][] lmsourcepts, double[][] lmtargetpts, Point2D test, double[] targetpoint){
		vtkPoints lmsource = createvtkpoints(lmsourcepts,sourceseq.getPixelSizeX(),sourceseq.getPixelSizeY(),sourceseq.getPixelSizeZ());
		vtkPoints lmtarget = createvtkpoints(lmtargetpts,sourceseq.getPixelSizeX(),sourceseq.getPixelSizeY(),sourceseq.getPixelSizeZ());

		final vtkThinPlateSplineTransform myvtkTransform = new vtkThinPlateSplineTransform();

	    myvtkTransform.SetSourceLandmarks(lmsource);
	    myvtkTransform.SetTargetLandmarks(lmtarget);
	    myvtkTransform.SetBasisToR2LogR();
	    
	    vtkTransformPolyDataFilter tr = new  vtkTransformPolyDataFilter();

	    vtkPoints mytestpoint = new vtkPoints();
	    mytestpoint.SetNumberOfPoints(1);
		mytestpoint.SetPoint(0,test.getX()*sourceseq.getPixelSizeX(), test.getY()*sourceseq.getPixelSizeY(),0.0);

		vtkPolyData mypoints = new vtkPolyData();
		mypoints.SetPoints(mytestpoint);

		vtkVertexGlyphFilter vertexfilter = new vtkVertexGlyphFilter();
		vertexfilter.SetInputData(mypoints);
		vertexfilter.Update();

		vtkPolyData sourcepolydata = new vtkPolyData();
		sourcepolydata.ShallowCopy(vertexfilter.GetOutput());

		tr.SetInputData(sourcepolydata);
		tr.SetTransform(myvtkTransform);
		tr.Update(); 

		vtkPolyData modifiedpoints = tr.GetOutput();
		double distanceinum = Math.sqrt(
			Math.pow(
				modifiedpoints.GetPoint(0)[0] - (targetpoint[0] * sourceseq.getPixelSizeX())
			, 2)
			+ Math.pow(
				modifiedpoints.GetPoint(0)[1] - (targetpoint[1] * sourceseq.getPixelSizeY())
			, 2)
		);
			
		return distanceinum * 1000;//in nm
	}

	@Override
	protected void execute() {
		boolean nonrigid = false;
		if (choiceinputsection.getValue().contains("compare with non Rigid")) {
			nonrigid = true;
		}
		
		stopflag = false;

		curve1 = new YIntervalSeries("Discrepancy");
		curve2 = new YIntervalSeries("Predicted Error");
		if (nonrigid)
			curve3 = new YIntervalSeries("Non rigid discrepancy");

		Dataset sourceDataset = datasetFactory.getFrom(source.getValue());
		Dataset targetDataset = datasetFactory.getFrom(target.getValue());

		if (sourceDataset.getN() != targetDataset.getN()) {
			System.out.println("source points different from target point");
			return;
		}

		treChecker.check(sourceDataset, targetDataset);

		final double FLE = uFLE.getValue();
		final int nbsimul = simulnumber.getValue();
		ProgressFrame myprogressbar = new ProgressFrame("Computing simulations...");
		myprogressbar.setLength(nbsimul * sourceDataset.getN());
		myprogressbar.setPosition(0);
		final DefaultBoxAndWhiskerCategoryDataset dataset2 = new DefaultBoxAndWhiskerCategoryDataset();

		try {
			if (!nonrigid) {
				write = new FileWriter("TREVALIDATION.csv");
				write.append("name;p;mc;dist;tre_measured;tre_predicted\n");
			} else {
				write = new FileWriter("TREVALIDATIONwithnorigid.csv");
				write.append("name;p;mc;dist;tre_measured;tre_predicted;tre_nonrigid\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < sourceDataset.getN(); i++) {
			if (stopflag) {
				break;
			}

			RegistrationErrorStatistics registrationErrorStatistics = new RegistrationErrorStatistics();
			ArrayList<double[]> datap = new ArrayList<>();
			ArrayList<double[]> datanr = new ArrayList<>();

			for (int mc = 0; mc < nbsimul; mc++) {
				myprogressbar.incPosition();

				Dataset sourceDatasetWithNoise = datasetGenerator.addNoise(sourceDataset, FLE);
				Dataset targetDatasetWithNoise = datasetGenerator.addNoise(targetDataset, FLE);

				Point leaveOutSourcePoint = sourceDatasetWithNoise.removePoint(i);
				Point leaveOutTargetPoint = targetDatasetWithNoise.removePoint(i);

//				if (nonrigid) {
//					Point2D testPointnr = new Point2D.Double(this.backupsourcepoints[i][0], this.backupsourcepoints[i][1]);
//					if (withFLE.getValue())
//						testPointnr = shakeOnePoint(sourceseq,FLE * 2, testPointnr);
//
//					double tre_nr = computenonrigid(sourcepoints,targetpoints,testPointnr,this.backuptargetpoints[i]);
//					double[] tre_nnr = new double[1];
//					tre_nnr[0] = tre_nr; // in case we want to add more infos later on
//					datanr.add(tre_nnr);
//				}

				Similarity similarity = rigidTransformationComputer.compute(sourceDatasetWithNoise, targetDatasetWithNoise);
				Point transformedLeaveOutSourcePoint = similarity.apply(leaveOutSourcePoint);

				RegistrationError registrationError = new RegistrationError(
					treComputerFactory.getFrom(sourceDatasetWithNoise, targetDatasetWithNoise).getExpectedSquareTRE(leaveOutTargetPoint),
					transformedLeaveOutSourcePoint.getDistance(leaveOutTargetPoint),
					leaveOutTargetPoint.getDistance(targetDatasetWithNoise.getBarycentre())
				);

				registrationErrorStatistics.add(registrationError);
			}

			double averageerrornr=0;
			double maxerrornr=0;
			double minerrornr=1000;

			List<Double> listnr=new ArrayList<>();

			for (int j = 0; j < datanr.size(); j++) {
				if (nonrigid){
					listnr.add(datanr.get(j)[0]);
					averageerrornr+=datanr.get(j)[0];
					maxerrornr=Math.max(maxerrornr, datanr.get(j)[0]);
					minerrornr=Math.min(minerrornr, datanr.get(j)[0]);
				}
			}

			curve1.add(
				registrationErrorStatistics.getDistanceToBarycentreStatistic().getAverage(),
				registrationErrorStatistics.getMeasuredErrorStatistic().getAverage(),
				registrationErrorStatistics.getMeasuredErrorStatistic().getMin(),
				registrationErrorStatistics.getMeasuredErrorStatistic().getMax()
			);

			curve2.add(
				registrationErrorStatistics.getDistanceToBarycentreStatistic().getAverage(),
				registrationErrorStatistics.getPredictedErrorStatistic().getAverage(),
				registrationErrorStatistics.getPredictedErrorStatistic().getMin(),
				registrationErrorStatistics.getPredictedErrorStatistic().getMax()
			);

			dataset2.add(registrationErrorStatistics.getMeasuredError(), "Left one out discrepancy (Ground truth TRE)", "ROI " + namep);
			dataset2.add(registrationErrorStatistics.getPredictedError(), "Predicted TRE ", "ROI " + namep);


			if (nonrigid){
				averageerrornr = averageerrornr / datanr.size();
				curve3.add(registrationErrorStatistics.getDistanceToBarycentreStatistic().getAverage(), averageerrornr, minerrornr, maxerrornr);
				dataset2.add(listnr,"Left one out discrepancy with Non Rigid transfo","ROI "+namep);
			}

			try {
				for (int j = 0; j < datap.size(); j++){
					double[] tt= datap.get(j);
					if (!nonrigid){
						write.append(namep+";"+ i +";"+j+";"+tt[2]+";"+tt[1]+";"+tt[0]+"\n");
					} else {
						write.append(namep+";"+ i +";"+j+";"+tt[2]+";"+tt[1]+";"+tt[0]+";"+datanr.get(j)[0]+"\n");
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
		try {
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		final YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
        dataset.addSeries(curve1);
        dataset.addSeries(curve2);
        if (nonrigid) {
			dataset.addSeries(curve3);
		}

        JFreeChart jfreechart=CreateChart(dataset,nbsimul,FLE);
		ChartPanel  chartPanel = new ChartPanel(jfreechart);
	    chartPanel.setFillZoomRectangle(true);
	    chartPanel.setMouseWheelEnabled(true);
	    chartPanel.setPreferredSize(new Dimension(500, 270));
	    mainPanel = GuiUtil.generatePanel("Graph");
		mainFrame = GuiUtil.generateTitleFrame("Real configuration Error MC Simulations", mainPanel, new Dimension(300, 100), true, true, true, true);
	    mainPanel.add(chartPanel);
	    mainFrame.pack();
	    addIcyFrame(mainFrame);
	    mainFrame.setVisible(true);
	    mainFrame.center();
	    mainFrame.requestFocus();
	    WhiskerPlot(dataset2);//}});
	    myprogressbar.close();
	}

	private void WhiskerPlot(DefaultBoxAndWhiskerCategoryDataset dataset2) {
		final CategoryAxis xAxis = new CategoryAxis("Left Out Point");
		final NumberAxis yAxis = new NumberAxis("in nanometers");
		yAxis.setAutoRangeIncludesZero(true);
		final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);
		renderer.setMeanVisible(true);

		renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		final CategoryPlot plot = new CategoryPlot(dataset2, xAxis, yAxis, renderer);
		plot.setDomainGridlinesVisible(true);
		plot.setRangePannable(true);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		final JFreeChart chart2 = new JFreeChart("Discrepancy distribution for TRE per ROI", JFreeChart.DEFAULT_TITLE_FONT, plot, true );
		final ChartPanel chartPanel = new ChartPanel(chart2);
		chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));
		mainPanel.add(chartPanel);
	}

	/**
	 *
	 * @param seq
	 * @param FLE
	 * @param point the point to be moved randomly in pixels
	 * @return point moved in pixels
	 */
	private Point2D shakeOnePoint(Sequence seq, double FLE, Point2D point) {

		double FLEp=FLE/(seq.getPixelSizeX()*1000);
		//double FLEpz=FLE/(seq.getPixelSizeZ()*1000);
		//double lower = -(Math.sqrt((FLEp*FLEp)/3)); // in order to have a distance max of FLE we consider a sphere of diameter fle
		double higher = (Math.sqrt((FLEp*FLEp)/3));

		point.setLocation(point.getX()+(generator.nextGaussian() * (higher/2)),point.getY()+(generator.nextGaussian() * (higher/2)));


		return point;

	}

	/**
	 * this method shake randomly (gaussian centered i 0, std FLE/2) the roi on the sequence in input
	 */
	private void shakeRois(Sequence seq, double FLE) {
		ArrayList<ROI> listfiducials = seq.getROIs();
		double FLEp=FLE/(seq.getPixelSizeX()*1000);
		double FLEpz=FLE/(seq.getPixelSizeZ()*1000);
		//double lower = -(Math.sqrt((FLEp*FLEp)/3)); // in order to have a distance max of FLE we consider a sphere of diameter fle
		double higher = (Math.sqrt((FLEp*FLEp)/3));
		double lowerz = -FLEpz;
		double higherz = FLEpz;
		
		for (ROI roi : listfiducials) {
			
			Point5D position = roi.getPosition5D();
			//position in pixels + ((random between 0 and 1 )* half FLE in nm - half FLE in nm )/(pixel size in nm)
			//position.setX(position.getX()+(Math.random() * (higher-lower)) + lower);
			//position.setY(position.getY()+(Math.random() * (higher-lower)) + lower);
			//Gaussian generation (generarted centered at 0 with std 1
			position.setX(position.getX()+(generator.nextGaussian() * (higher/2)));
			position.setY(position.getY()+(generator.nextGaussian() * (higher/2)));
			if (mode3D){
				position.setZ(position.getZ()+(Math.random() * (higherz-lowerz)) + lowerz);
			}
			else
				position.setZ(0);
			roi.setPosition5D(position);
		}
		
	}

	private static JFreeChart CreateChart(YIntervalSeriesCollection dataset,int mc,double fle) {
		 JFreeChart chart = ChartFactory.createXYLineChart(
		            "Discrepancy vs error for "+mc+"  simulations, FLE= "+fle+ "nm",      // chart title
		            "Distance from the center of gravity for the point removed ",                      // x axis label
		            "in nm",                      // y axis label
		            dataset,                  // data
		            PlotOrientation.VERTICAL,
		           true,                     // include legend
		            true,                     // tooltips
		            false                     // urls
		        );
        chart.setBackgroundPaint(Color.white);
        XYPlot xyplot = (XYPlot) chart.getPlot();
        xyplot.setInsets(new RectangleInsets(5D, 5D, 5D, 20D));
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        DeviationRenderer deviationrenderer = new DeviationRenderer(true, false);
        deviationrenderer.setSeriesStroke(0, new BasicStroke(3F, 1, 1));
        deviationrenderer.setSeriesShapesVisible(0, true);
        deviationrenderer.setSeriesShapesVisible(1, true);
        deviationrenderer.setSeriesStroke(1, new BasicStroke(3F, 1, 1));
        deviationrenderer.setSeriesFillPaint(0, new Color(255, 200, 200));
        deviationrenderer.setSeriesFillPaint(1, new Color(200, 200, 255));
        xyplot.setRenderer(deviationrenderer);
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chart;
    }

	@Override
	protected void initialize() {
		EzLabel textinfo1 = new EzLabel(
				"Give information about error computation, usind leave one out as well.");
		new ToolTipFrame(    			
    			"<html>"+
    			"<br>This plugin compute from a set of matching points: "+
    			"<br> <li> The accuracy with Monte Carlo Simulations "+
    			"<br>(moving randomly all points around their initial position with the FLE error),"+
    			"<br>  of the registration error of a point against its target position" +
    			"<br> when the point is left OUT the set of point for the registration (i.e N-1 points are used)</li>"+
    			"<br><li> The predicted average  error on the same point, computed <b>without any ground truth</b></li> "+
    			"<br><b> FLE </b> is the localization error you ca expect, i.e basically the resolution of your image "+
    			"<br>(around 400 nm i Fluoresence for exemple), "+
    			" <br>ROI Points should have similar names in both source and target image, such as Point 1, Point 2,..)"+
    			"</html>"
    			);
		
		source = new EzVarSequence("Select Image with Roi sets ");
		addEzComponent(source);
		addEzComponent(uFLE);
		addEzComponent(simulnumber);
		addEzComponent(textinfo1);
		addEzComponent(choiceinputsection);
		addEzComponent(withFLE);
	}
				
//	private boolean CheckTREvsFREmc(PointsPair leftpoint, String name, ArrayList<double[]> datap ) {
//		//For each ROI
//		boolean check=false;
//		//Compute FRE and compute TRE
//		//return true when one has a tre > observed error
//		double error=0; //in nm
//		double predictederror=0; //in nm
//
//		double FLEmax = uFLE.getValue();
//		//System.out.println("Left Point: Max localization error FLE "+FLEmax+" nm");
//		TargetRegistrationErrorMap ComputeFRE = new TargetRegistrationErrorMap();
//		Dataset dataset = ComputeFRE.ReadFiducials(target);
//		TREComputer treComputer = ComputeFRE.PreComputeTRE(dataset);
//		error=leftpoint.getDiffinpixels();
//		error=error*sourceseq.getPixelSizeX()*1000; // in um , to be converted in nm
//		predictederror = treComputer.compute(
//			FLEmax,
//			new Point(new Matrix(new double[][] {
//				{ (int)leftpoint.first.getX() },
//				{ (int)leftpoint.first.getY() }
//			}))
//		);
//		//System.out.println(name+" Discrepancy in nm: "+error+ "vs Predicted error in nm: "+predictederror);
//		double[] mytab=new double[3];
//		mytab[0]=predictederror;
//		mytab[1]=error;
//		mytab[2]=distancetogravitycenter(leftpoint.second,target);
//		datap.add(mytab);
//		return check;
//	}



					
	@Override
	public void stopExecution(){
		stopflag=true;
	}
}
