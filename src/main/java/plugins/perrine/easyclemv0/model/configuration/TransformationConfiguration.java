package plugins.perrine.easyclemv0.model.configuration;

import plugins.perrine.easyclemv0.model.TransformationType;

public class TransformationConfiguration {
    private TransformationType transformationType;
    private boolean showGrid;

    public TransformationConfiguration(TransformationType transformationType, boolean showGrid) {
        this.transformationType = transformationType;
        this.showGrid = showGrid;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public boolean isShowGrid() {
        return showGrid;
    }
}
