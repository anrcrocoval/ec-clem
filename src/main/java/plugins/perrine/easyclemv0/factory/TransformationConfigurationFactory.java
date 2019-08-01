package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.TransformationType;
import plugins.perrine.easyclemv0.model.configuration.TransformationConfiguration;

import javax.inject.Inject;

public class TransformationConfigurationFactory {

    @Inject
    public TransformationConfigurationFactory() {}

    public TransformationConfiguration getFrom(TransformationType transformationType, boolean showgrid) {
        return new TransformationConfiguration(transformationType, showgrid);
    }
}
