package plugins.perrine.easyclemv0.workspace;

public class WorkspaceState {

    private boolean flagReadyToMove;
    private boolean done;

    public WorkspaceState(boolean flagReadyToMove, boolean done) {
        this.flagReadyToMove = flagReadyToMove;
        this.done = done;
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
