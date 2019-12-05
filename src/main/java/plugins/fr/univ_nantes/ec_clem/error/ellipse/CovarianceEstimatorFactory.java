package plugins.fr.univ_nantes.ec_clem.error.ellipse;

import plugins.fr.univ_nantes.ec_clem.error.ellipse.affine.AffineCovarianceEstimator;
import plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid.RigidCovarianceEstimator;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;
import javax.inject.Inject;

public class CovarianceEstimatorFactory {
    private AffineCovarianceEstimator affineCovarianceEstimator;
    private RigidCovarianceEstimator rigidCovarianceEstimator;

    @Inject
    public CovarianceEstimatorFactory(AffineCovarianceEstimator affineCovarianceEstimator, RigidCovarianceEstimator rigidCovarianceEstimator) {
        this.affineCovarianceEstimator = affineCovarianceEstimator;
        this.rigidCovarianceEstimator = rigidCovarianceEstimator;
    }

    public CovarianceEstimator getFrom(TransformationType type) {
        switch (type) {
            case AFFINE: return affineCovarianceEstimator;
            case RIGID: return rigidCovarianceEstimator;
            default: throw new RuntimeException("Not implemented");
        }
    }
}
