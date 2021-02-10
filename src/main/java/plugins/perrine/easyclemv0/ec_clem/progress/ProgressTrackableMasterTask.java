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
package plugins.perrine.easyclemv0.ec_clem.progress;

import java.util.ArrayList;
import java.util.List;

public abstract class ProgressTrackableMasterTask extends ProgressReport implements ProgressTrackable {
    private List<ProgressTrackable> tasks;

    public ProgressTrackableMasterTask() {
        this.tasks = new ArrayList<>();
    }

    public ProgressTrackableMasterTask add(ProgressTrackable task) {
        tasks.add(task);
        super.setChanged();
        super.notifyObservers(task);
        return this;
    }

    public void visit(ProgressManager progressManager) {
        super.addObserver(progressManager);
        for(ProgressTrackable progressTrackable : tasks) {
            progressManager.subscribe(progressTrackable);
        }
    }

    @Override
    public ProgressReport getProgress() {
        return this;
    }

    @Override
    public int getCompleted() {
        return (int) tasks.stream().filter(task -> task.getProgress().isCompleted()).count();
    }

    @Override
    public int getTotal() {
        return tasks.size();
    }

    protected List<ProgressTrackable> getTaskList() {
        return tasks;
    }
}
