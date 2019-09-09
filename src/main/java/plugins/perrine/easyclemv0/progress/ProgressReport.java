package plugins.perrine.easyclemv0.progress;

import java.util.Observable;

public abstract class ProgressReport extends Observable {
    public abstract int getCompleted();
    public abstract int getTotal();

    public boolean isCompleted() {
        return !(getCompleted() < getTotal());
    }
}
