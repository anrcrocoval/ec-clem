package plugins.perrine.easyclemv0.workspace;

import dagger.Component;

@Component
public interface UndoComponent {
    void inject(Undo undo);
}
