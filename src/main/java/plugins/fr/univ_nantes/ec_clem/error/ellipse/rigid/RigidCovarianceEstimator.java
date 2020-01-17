package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.error.ellipse.CovarianceEstimator;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import plugins.fr.univ_nantes.ec_clem.matrix.MatrixUtil;
import plugins.fr.univ_nantes.ec_clem.registration.RegistrationParameter;
import plugins.fr.univ_nantes.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;

import javax.inject.Inject;
import java.util.Arrays;

public class RigidCovarianceEstimator implements CovarianceEstimator {

    private MatrixUtil matrixUtil;
    private RegistrationParameterFactory transformationFactory;
    private InverseFisherInformationMatrix2DEstimator inverseFisherInformationMatrix2DEstimator;
    private JacobianMatrix2D jacobianMatrix2D;

    @Inject
    public RigidCovarianceEstimator(
        RegistrationParameterFactory transformationFactory,
        MatrixUtil matrixUtil,
        InverseFisherInformationMatrix2DEstimator inverseFisherInformationMatrix2DEstimator,
        JacobianMatrix2D jacobianMatrix2D
    ) {
        this.transformationFactory = transformationFactory;
        this.matrixUtil = matrixUtil;
        this.inverseFisherInformationMatrix2DEstimator = inverseFisherInformationMatrix2DEstimator;
        this.jacobianMatrix2D = jacobianMatrix2D;
    }

    @Override
    public Matrix getCovariance(TransformationSchema transformationSchema, Point zSource) {
        RegistrationParameter from = transformationFactory.getFrom(transformationSchema);
        Matrix lambda = from.getNoiseCovariance();
//        Matrix lambdaInv = matrixUtil.pseudoInverse(lambda);

//        Matrix jtt = lambdaInv.times(-1 * transformationSchema.getFiducialSet().getN());
//        Matrix jto = new Matrix(2, 1, 0);
//        Matrix joo = new Matrix(1, 1, 0);

//        for(int i = 0; i < transformationSchema.getFiducialSet().getN(); i++) {
//            Matrix x = new Matrix(new double[][]{
//                { transformationSchema.getFiducialSet().getSourceDataset().getPoint(i).get(1) },
//                { -transformationSchema.getFiducialSet().getSourceDataset().getPoint(i).get(0) }
//            });
//            jto.plusEquals(lambdaInv.times(x));
//            joo.plusEquals(x.transpose().times(lambdaInv).times(x).times(-1));
//        }

//        Matrix J = new Matrix(3, 3);
//        J.setMatrix(0, 1, 0, 1, jtt.times(-1));
//        J.setMatrix(0, 1, 2, 2, jto.times(-1));
//        J.setMatrix(2, 2, 0, 1, jto.transpose().times(-1));
//        J.setMatrix(2, 2, 2, 2, joo.times(-1));

//        Matrix sigma = matrixUtil.pseudoInverse(J);
        Matrix sigma = inverseFisherInformationMatrix2DEstimator.getInverseFisherInformationMatrix(transformationSchema.getFiducialSet(), from);
        Matrix jacobian = jacobianMatrix2D.getJacobian(zSource, from);

        Matrix E = jacobian.times(sigma).times(jacobian.transpose());

//        Matrix E = new Matrix(2,2);
//        E.set(0, 0,
//            zSource.get(1) * zSource.get(1) * sigma.get(2, 2) - 2 * zSource.get(1) * sigma.get(0, 2) + sigma.get(0, 0)
//        );
//        E.set(0, 1,
//            -1 * zSource.get(0) * zSource.get(1) * sigma.get(2, 2) - zSource.get(1) * sigma.get(1, 2) + zSource.get(0) * sigma.get(0, 2) + sigma.get(0, 1)
//        );
////        E.set(1, 0, E.get(0, 1) * -1);
//        E.set(1, 0, E.get(0, 1));
//        E.set(1, 1,
//            zSource.get(0) * zSource.get(0) * sigma.get(2, 2) + 2 * zSource.get(0) * sigma.get(1, 2) + sigma.get(1, 1)
//        );
        return E.plus(lambda);
    }
}
