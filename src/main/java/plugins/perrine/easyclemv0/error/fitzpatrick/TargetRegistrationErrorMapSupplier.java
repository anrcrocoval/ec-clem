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
package plugins.perrine.easyclemv0.error.fitzpatrick;

import icy.image.IcyBufferedImage;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.progress.*;
import plugins.perrine.easyclemv0.sequence.SequenceFactory;
import plugins.perrine.easyclemv0.sequence.SequenceSize;
import plugins.stef.tools.overlay.ColorBarOverlay;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class TargetRegistrationErrorMapSupplier extends ProgressTrackableChildTask implements Supplier<Sequence> {

    private SequenceFactory sequenceFactory;
    private SequenceSize sequenceSize;
    private TREComputer treComputer;

    private CompletionService<IcyBufferedImage> completionService = new ExecutorCompletionService<>(
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    );

    public TargetRegistrationErrorMapSupplier(SequenceSize sequenceSize, TREComputer treComputer) {
        super(sequenceSize.get(DimensionId.Z).getSize());
        DaggerTargetRegistrationErrorMapSupplierComponent.builder().build().inject(this);
        this.sequenceSize = sequenceSize;
        this.treComputer = treComputer;
    }

    @Override
    public Sequence get() {
        Map<Future<IcyBufferedImage>, Integer> resultMap = new HashMap<>();
        Sequence newSequence = sequenceFactory.getFrom(sequenceSize);
        newSequence.beginUpdate();
        for (int z = 0; z < sequenceSize.get(DimensionId.Z).getSize(); z++) {
            final double finalz = z;
            Point point = new Point(sequenceSize.getN());
            resultMap.put(completionService.submit(() -> {
                float[] dataArray = new float[sequenceSize.get(DimensionId.X).getSize() * sequenceSize.get(DimensionId.Y).getSize()];
                for (int x = 0; x < sequenceSize.get(DimensionId.X).getSize(); x++) {
                    for (int y = 0; y < sequenceSize.get(DimensionId.Y).getSize(); y++) {
                        point.getMatrix().set(0, 0, x * sequenceSize.get(DimensionId.X).getPixelSizeInMicrometer());
                        point.getMatrix().set(1, 0, y * sequenceSize.get(DimensionId.Y).getPixelSizeInMicrometer());
                        point.getMatrix().set(2, 0, finalz * sequenceSize.get(DimensionId.Z).getPixelSizeInMicrometer());
                        dataArray[(y * sequenceSize.get(DimensionId.X).getSize()) + x] = (float) treComputer.getExpectedSquareTRE(point);
                    }
                }

                return new IcyBufferedImage(sequenceSize.get(DimensionId.X).getSize(), sequenceSize.get(DimensionId.Y).getSize(), dataArray);
            }), z);
        }

        while (!resultMap.isEmpty()) {
            try {
                Future<IcyBufferedImage> take = completionService.take();
                int offset = resultMap.remove(take);
                newSequence.setImage(0, offset, take.get());
                super.incrementCompleted();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        newSequence.setAutoUpdateChannelBounds(false);
        newSequence.setName("Prediction of registration only error in nanometers (if calibration settings were correct)");
        ColorBarOverlay mycolorbar = new ColorBarOverlay(null);
        mycolorbar.setDisplayMinMax(true);
        newSequence.addOverlay(mycolorbar);
        newSequence.endUpdate();
        return newSequence;
    }

    @Inject
    public void setSequenceFactory(SequenceFactory sequenceFactory) {
        this.sequenceFactory = sequenceFactory;
    }
}
