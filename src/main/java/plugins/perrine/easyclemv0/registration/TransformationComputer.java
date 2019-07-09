package plugins.perrine.easyclemv0.registration;

import plugins.perrine.easyclemv0.model.FiducialSet;
import plugins.perrine.easyclemv0.model.transformation.Transformation;

public interface TransformationComputer {
    Transformation compute(FiducialSet fiducialSet);
}
