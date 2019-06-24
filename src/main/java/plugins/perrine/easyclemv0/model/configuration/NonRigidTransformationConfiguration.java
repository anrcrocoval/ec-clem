package plugins.perrine.easyclemv0.model.configuration;

import plugins.perrine.easyclemv0.model.TransformationType;

public class NonRigidTransformationConfiguration extends TransformationConfiguration {
    private boolean showGrid;

    public NonRigidTransformationConfiguration(boolean showGrid) {
        super(TransformationType.NON_RIGID);
        this.showGrid = showGrid;
    }

    public boolean isShowGrid() {
        return showGrid;
    }
}
