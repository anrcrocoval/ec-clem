package plugins.perrine.easyclemv0.test.registration;

import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.transformation.Transformation;

public interface TransformationComputer {
    Transformation compute(FiducialSet fiducialSet);
}
