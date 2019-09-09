package plugins.perrine.easyclemv0.workspace;

import dagger.Component;

@Component
public interface WorkspaceTransformerComponent {
    void inject(WorkspaceTransformer workspaceTransformer);
}