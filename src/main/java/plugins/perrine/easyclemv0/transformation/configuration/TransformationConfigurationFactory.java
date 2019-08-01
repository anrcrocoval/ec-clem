package plugins.perrine.easyclemv0.transformation.configuration;

import plugins.perrine.easyclemv0.transformation.schema.TransformationType;

import javax.inject.Inject;

public class TransformationConfigurationFactory {

    @Inject
    public TransformationConfigurationFactory() {}

    public TransformationConfiguration getFrom(TransformationType transformationType, boolean showgrid) {
        return new TransformationConfiguration(transformationType, showgrid);
    }
}
