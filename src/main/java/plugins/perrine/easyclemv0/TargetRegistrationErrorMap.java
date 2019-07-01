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


import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.ProgressFrame;
//import icy.gui.lut.LUTViewer;
import icy.gui.viewer.Viewer;
import icy.image.IcyBufferedImage;
import icy.image.colormap.FireColorMap;

import icy.image.lut.LUT;

import icy.roi.ROI;

import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import icy.type.point.Point5D;
//import ij.plugin.filter.LutViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;


//import plugins.kernel.roi.roi2d.ROI2DEllipse;
import Jama.Matrix;
import org.apache.commons.math3.util.CombinatoricsUtils;
import plugins.kernel.roi.descriptor.measure.ROIMassCenterDescriptorsPlugin;
//import icy.main.Icy;
//import icy.math.Scaler;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.factory.SequenceSizeFactory;
import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.inertia.InertiaMatrixComputer;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.roi.RoiProcessor;
import plugins.stef.tools.overlay.ColorBarOverlay;
/** author: Perrine.paul-gilloteaux@curie.fr
 * purpose: Compute TRE in each image points from FLE fiducial localisation error
 *
 */
public class TargetRegistrationErrorMap implements Runnable {

    private ProgressFrame myprogressbar;
    private Sequence sequence;
    private IcyBufferedImage image;

    private SequenceSizeFactory sequenceSizeFactory = new SequenceSizeFactory();

    private RoiProcessor roiProcessor = new RoiProcessor();
//    private TREComputerFactory treComputerFactory = new TREComputerFactory();

    private TREComputer treComputer;

    private CompletionService<IcyBufferedImage> completionService = new ExecutorCompletionService<>(
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    );

    public TargetRegistrationErrorMap(TREComputer treComputer) {
        this.treComputer = treComputer;
    }

    /**
     * will be run in plugin mode:
     *  set an progress bar,
     *  compute the vector f of distance
     *  and then compute the TRE in each point of the image
     *  recalibrate the image in pixel size to have it coherent with the target image
     *  display a lut where error <100 nm emphasized in green
     */
    @Override
    public void run() {
        //added to avoid a bug report when no ROI or not enough ROI.
        if (this.sequence.getROIs().size() < 3) {
            MessageDialog.showDialog("You need at least 4 points to compute an error map ");
        }
//        Dataset sourceDataset = ReadFiducials(this.sequence);
        //fiducial is read in nm
        myprogressbar = new ProgressFrame("EasyCLEM is computing Error Map");
        myprogressbar.setLength(sequence.getSizeZ());
        myprogressbar.setPosition(0);
        myprogressbar.setMessage("EasyCLEM was Precomputing Inertia Matrix done");

//        TREComputer treComputer = treComputerFactory.getFrom(sourceDataset, targetDataset);

        myprogressbar.setMessage("EasyCLEM is computing Error Map");
        final Sequence TREMAP = ComputeTREMAP(sequence, image, treComputer);
        myprogressbar.close();
        double sizex = sequence.getPixelSizeX();
        double sizey = sequence.getPixelSizeY();
        double sizez = sequence.getPixelSizeZ();
        if (TREMAP == null) {
            MessageDialog.showDialog("No active image");
            return;
        }
        TREMAP.setPixelSizeX(sizex);
        TREMAP.setPixelSizeY(sizey);
        TREMAP.setPixelSizeZ(sizez);
        TREMAP.setAutoUpdateChannelBounds(false);
        TREMAP.endUpdate();
        TREMAP.setName("Prediction of registration only error in nanometers (if calibration settings were correct)");
        ColorBarOverlay mycolorbar = new ColorBarOverlay(null);
        mycolorbar.setDisplayMinMax(true);
        TREMAP.addOverlay(mycolorbar);
        ThreadUtil.invokeLater(new Runnable() {
            public void run() {
                Viewer myviewer = new Viewer(TREMAP);
                LUT mylut = myviewer.getLut();
                mylut.getLutChannel(0).setColorMap(new FireColorMap(), false);
                System.out.println("done");
            }
        });
    }

    public void apply(Sequence sequence, IcyBufferedImage image) {
        this.sequence = sequence;
        this.image = image;
        new Thread(this).start();
    }

    private Sequence ComputeTREMAP(Sequence sequence, IcyBufferedImage image, TREComputer treComputer) {
        Sequence newsequence = new Sequence();
        if (image == null) {
            return null;
        }

        Map<Future<IcyBufferedImage>, Integer> resultMap = new HashMap<>();

        SequenceSize sequenceSize = sequenceSizeFactory.getFrom(sequence);

        for (int z = 0; z < sequence.getSizeZ(); z++) {
            Point point = new Point(sequenceSize.getN());
            resultMap.put(completionService.submit(() -> {
                float[] dataArray = new float[image.getSizeX() * image.getSizeY()];

                for (int x = 0; x < image.getSizeX(); x++) {
                    for (int y = 0; y < image.getSizeY(); y++) {
                        point.getMatrix().set(0, 0, x);
                        point.getMatrix().set(1, 0, y);
                        dataArray[image.getOffset(x, y)] = (float) treComputer.getExpectedSquareTRE(point);
                    }
                }

                return new IcyBufferedImage(sequence.getSizeX(), sequence.getSizeY(), dataArray);
            }), z);
        }

        while (!resultMap.isEmpty()) {
            try {
                Future<IcyBufferedImage> take = completionService.take();
                int offset = resultMap.remove(take);
                newsequence.setImage(0, offset, take.get());
                myprogressbar.incPosition();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return newsequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }
}
