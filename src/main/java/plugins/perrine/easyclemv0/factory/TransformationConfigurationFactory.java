package plugins.perrine.easyclemv0.factory;

import plugins.perrine.easyclemv0.model.configuration.NonRigidTransformationConfiguration;
import plugins.perrine.easyclemv0.model.configuration.RigidTransformationConfiguration;
import plugins.perrine.easyclemv0.model.configuration.TransformationConfiguration;

public class TransformationConfigurationFactory {

    public TransformationConfiguration getFrom(boolean nonrigid, boolean showgrid) {
        if(!nonrigid) {
            return new RigidTransformationConfiguration();
        } else {
            return new NonRigidTransformationConfiguration(showgrid);
        }
    }
}
