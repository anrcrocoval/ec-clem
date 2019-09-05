package plugins.perrine.easyclemv0.progress;

public abstract class ProgressReport {

    public abstract int getCompleted();
    public abstract int getTotal();

    public boolean isCompleted() {
        return !(getCompleted() < getTotal());
    }

    public float getPercentCompleted() {
        return (float) getCompleted() / getTotal() * 100;
    }
}
