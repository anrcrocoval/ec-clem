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
package plugins.perrine.easyclemv0.error;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.ProgressFrame;
import icy.gui.viewer.Viewer;
import icy.image.IcyBufferedImage;
import icy.image.colormap.FireColorMap;
import icy.image.lut.LUT;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import plugins.perrine.easyclemv0.sequence.SequenceSizeFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.stef.tools.overlay.ColorBarOverlay;

import javax.inject.Inject;

public class TargetRegistrationErrorMap implements Runnable {

    private ProgressFrame myprogressbar;
    private Sequence sequence;
    private IcyBufferedImage image;
    private SequenceSizeFactory sequenceSizeFactory;
    private TREComputer treComputer;

    private CompletionService<IcyBufferedImage> completionService = new ExecutorCompletionService<>(
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    );

    @Inject
    public TargetRegistrationErrorMap(SequenceSizeFactory sequenceSizeFactory) {
        this.sequenceSizeFactory = sequenceSizeFactory;
    }

    public TargetRegistrationErrorMap setTreComputer(TREComputer treComputer) {
        this.treComputer = treComputer;
        return this;
    }

    @Override
    public void run() {
        if (sequence.getROIs().size() < 3) {
            MessageDialog.showDialog("You need at least 4 points to compute an error map ");
        }

        myprogressbar = new ProgressFrame("EasyCLEM is computing Error Map");
        myprogressbar.setLength(sequence.getSizeZ());
        myprogressbar.setPosition(0);
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
        newsequence.setPixelSizeX(sequence.getPixelSizeX());
        newsequence.setPixelSizeY(sequence.getPixelSizeY());
        newsequence.setPixelSizeZ(sequence.getPixelSizeZ());
        if (image == null) {
            return null;
        }

        Map<Future<IcyBufferedImage>, Integer> resultMap = new HashMap<>();
        SequenceSize sequenceSize = sequenceSizeFactory.getFrom(sequence);

        for (int z = 0; z < sequence.getSizeZ(); z++) {
            final double finalz = z;
            Point point = new Point(sequenceSize.getN());
            resultMap.put(completionService.submit(() -> {
                float[] dataArray = new float[image.getSizeX() * image.getSizeY()];
                for (int x = 0; x < image.getSizeX(); x++) {
                    for (int y = 0; y < image.getSizeY(); y++) {
                        point.getMatrix().set(0, 0, x * sequence.getPixelSizeX());
                        point.getMatrix().set(1, 0, y * sequence.getPixelSizeY());
                        point.getMatrix().set(2, 0, finalz * sequence.getPixelSizeZ());
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
