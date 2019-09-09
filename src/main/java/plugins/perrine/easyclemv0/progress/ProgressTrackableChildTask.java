package plugins.perrine.easyclemv0.progress;

public abstract class ProgressTrackableChildTask implements ProgressTrackable {
    private ChildProgressReport childProgressReport;

    public ProgressTrackableChildTask(int total) {
        this.childProgressReport = new ChildProgressReport(total);
    }

    @Override
    public ProgressReport getProgress() {
        return childProgressReport;
    }

    @Override
    public void visit(ProgressManager progressManager) {
        progressManager.add(this);
    }

    public void incrementCompleted() {
        childProgressReport.incrementCompleted();
    }
}
