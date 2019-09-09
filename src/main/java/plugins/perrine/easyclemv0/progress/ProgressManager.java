package plugins.perrine.easyclemv0.progress;

import java.util.Observer;

public abstract class ProgressManager implements Observer {

    public abstract void add(ProgressTrackable progressTrackable);
    public void subscribe(ProgressTrackable progressTrackable) {
        progressTrackable.visit(this);
    }
}
