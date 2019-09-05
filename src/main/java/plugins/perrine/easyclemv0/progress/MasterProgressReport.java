package plugins.perrine.easyclemv0.progress;

import java.util.ArrayList;
import java.util.List;

public class MasterProgressReport extends ProgressReport implements ProgressTrackable {
    private List<ProgressTrackable> tasks;

    public MasterProgressReport() {
        this.tasks = new ArrayList<>();
    }

    public List<ProgressTrackable> getChildReports() {
        return tasks;
    }

    public MasterProgressReport add(ProgressTrackable task) {
        tasks.add(task);
        return this;
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
