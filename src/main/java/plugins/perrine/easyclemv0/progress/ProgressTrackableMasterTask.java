package plugins.perrine.easyclemv0.progress;

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
}
