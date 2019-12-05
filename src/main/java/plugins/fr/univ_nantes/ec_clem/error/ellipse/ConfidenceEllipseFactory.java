package plugins.fr.univ_nantes.ec_clem.error.ellipse;

import Jama.EigenvalueDecomposition;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;
import plugins.fr.univ_nantes.ec_clem.transformation.TransformationFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;

public class ConfidenceEllipseFactory {

    private TransformationFactory transformationFactory;
    private CovarianceEstimatorFactory covarianceEstimatorFactory;
    private HotellingEstimator hotellingEstimator;

    @Inject
    public ConfidenceEllipseFactory(
        TransformationFactory transformationFactory,
        CovarianceEstimatorFactory covarianceEstimatorFactory,
        HotellingEstimator hotellingEstimator
    ) {
        this.transformationFactory = transformationFactory;
        this.covarianceEstimatorFactory = covarianceEstimatorFactory;
        this.hotellingEstimator = hotellingEstimator;
    }

    public Ellipse getFrom(Point zSource, TransformationSchema transformationSchema, double alpha) {
        Transformation transformation = transformationFactory.getFrom(transformationSchema);
        Point zTarget = transformation.apply(zSource);
        EigenvalueDecomposition eigenValueDecomposition = new EigenvalueDecomposition(
            covarianceEstimatorFactory.getFrom(transformationSchema.getTransformationType()).getCovariance(transformationSchema, zSource)
                .times(
                    hotellingEstimator.getFrom(transformationSchema.getFiducialSet(), alpha)
                )
        );
        return new Ellipse(
            eigenValueDecomposition.getRealEigenvalues(),
            eigenValueDecomposition.getV(),
            zTarget
        );
    }
}
