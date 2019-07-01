package plugins.perrine.easyclemv0.model;

public class WorkspaceState {

    private boolean pause;
    private boolean flagReadyToMove;
    private boolean done;

    public WorkspaceState(boolean pause, boolean flagReadyToMove, boolean done) {
        this.pause = pause;
        this.flagReadyToMove = flagReadyToMove;
        this.done = done;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isFlagReadyToMove() {
        return flagReadyToMove;
    }

    public void setFlagReadyToMove(boolean flagReadyToMove) {
        this.flagReadyToMove = flagReadyToMove;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
