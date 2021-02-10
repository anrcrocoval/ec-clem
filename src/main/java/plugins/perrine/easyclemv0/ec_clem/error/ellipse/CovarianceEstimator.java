package plugins.perrine.easyclemv0.ec_clem.error.ellipse;

import Jama.Matrix;
import plugins.perrine.easyclemv0.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.ec_clem.transformation.schema.TransformationSchema;

public interface CovarianceEstimator {
    Matrix getCovariance(TransformationSchema transformationSchema, Point zSource);
}
