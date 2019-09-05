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
package plugins.perrine.easyclemv0.ui;

import icy.gui.frame.progress.ProgressFrame;
import plugins.perrine.easyclemv0.progress.MasterProgressReport;
import plugins.perrine.easyclemv0.progress.ProgressReport;
import plugins.perrine.easyclemv0.progress.ProgressTrackable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProgressBarManager {

    private Map<ProgressTrackable, ProgressFrame> bars;
    private Map<ProgressTrackable, ScheduledFuture> schedules;
    private ScheduledExecutorService scheduledExecutorService;

    @Inject
    public ProgressBarManager() {
        bars = new HashMap<>();
        schedules = new HashMap<>();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void subscribe(MasterProgressReport masterReport) {
        add(masterReport);
        for(ProgressTrackable childReport : masterReport.getChildReports()) {
            add(childReport);
        }
    }

    private void add(ProgressTrackable progressTrackable) {
        ProgressFrame progressFrame = new ProgressFrame(progressTrackable.getClass().getSimpleName());
        progressFrame.setLength(progressTrackable.getProgress().getTotal());
        progressFrame.setPosition(0);
        bars.put(progressTrackable, progressFrame);
        schedules.put(progressTrackable, scheduledExecutorService.scheduleWithFixedDelay(() -> { handle(progressTrackable); }, 0, 1, TimeUnit.SECONDS));
    }

    private void handle(ProgressTrackable task) {
        ProgressReport progress = task.getProgress();
        bars.get(task).setPosition(progress.getCompleted());
        bars.get(task).setMessage(String.format(
                "%s - %d completed / %d total",
                task.getClass().getSimpleName(),
                progress.getCompleted(),
                progress.getTotal()
        ));

        if(progress.isCompleted()) {
            schedules.remove(task).cancel(false);
            bars.remove(task).close();
        }
    }
}
