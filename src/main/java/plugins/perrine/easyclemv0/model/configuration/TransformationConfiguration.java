package plugins.perrine.easyclemv0.model.configuration;

import plugins.perrine.easyclemv0.model.TransformationType;

public abstract class TransformationConfiguration {
    private TransformationType transformationType;

    public TransformationConfiguration(TransformationType transformationType) {
        this.transformationType = transformationType;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }
}
