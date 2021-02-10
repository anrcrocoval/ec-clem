package plugins.perrine.ec_clem.ec_clem.error.ellipse;

import Jama.Matrix;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;

public interface CovarianceEstimator {
    Matrix getCovariance(TransformationSchema transformationSchema, Point zSource);
}
