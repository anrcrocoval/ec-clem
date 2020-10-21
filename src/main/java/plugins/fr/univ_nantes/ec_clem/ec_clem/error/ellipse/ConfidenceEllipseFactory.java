package plugins.fr.univ_nantes.ec_clem.ec_clem.error.ellipse;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Transformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;

public class ConfidenceEllipseFactory {

    private RegistrationParameterFactory transformationFactory;
    private CovarianceEstimatorFactory covarianceEstimatorFactory;
    private HotellingEstimator hotellingEstimator;

    @Inject
    public ConfidenceEllipseFactory(
        RegistrationParameterFactory transformationFactory,
        CovarianceEstimatorFactory covarianceEstimatorFactory,
        HotellingEstimator hotellingEstimator
    ) {
        this.transformationFactory = transformationFactory;
        this.covarianceEstimatorFactory = covarianceEstimatorFactory;
        this.hotellingEstimator = hotellingEstimator;
    }

    public Ellipse getFrom(Point zSource, TransformationSchema transformationSchema, double alpha) {
        Transformation transformation = transformationFactory.getFrom(transformationSchema).getTransformation();
        Point zTarget = transformation.apply(zSource);
        Matrix covariance = covarianceEstimatorFactory.getFrom(transformationSchema.getTransformationType()).getCovariance(transformationSchema, zSource);
        return getFrom(zTarget, transformationSchema.getFiducialSet(), covariance, alpha);
    }

    public Ellipse getFrom(Point zPredictedTarget, FiducialSet fiducialSet, Matrix covariance, double alpha) {
        EigenvalueDecomposition eigenValueDecomposition = new EigenvalueDecomposition(
            covariance
                .times(hotellingEstimator.getFrom(fiducialSet, alpha))
                .times((double) fiducialSet.getN() / (double) (fiducialSet.getN() - fiducialSet.getSourceDataset().getDimension() - 1))
        );
        System.out.println("d= "+ fiducialSet.getSourceDataset().getDimension());
        System.out.println("x y z= "+ fiducialSet.getSourceDataset().getPoint(0).get(0)+" " +fiducialSet.getSourceDataset().getPoint(0).get(1));
        System.out.println("e0"+eigenValueDecomposition.getRealEigenvalues()[0]);
        System.out.println("e1"+eigenValueDecomposition.getRealEigenvalues()[1]);
        //System.out.println("e2"+eigenValueDecomposition.getRealEigenvalues()[2]);
        return new Ellipse(
            eigenValueDecomposition.getRealEigenvalues(),
            eigenValueDecomposition.getV(),
            zPredictedTarget
        );
    }
}
