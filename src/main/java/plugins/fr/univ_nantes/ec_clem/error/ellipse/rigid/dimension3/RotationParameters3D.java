package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid.dimension3;

import Jama.Matrix;
import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.atan2;
import static java.lang.Math.sin;

public class RotationParameters3D {

    @Inject
    public RotationParameters3D() {}

    public double[] getZYXEulerParameters(Similarity similarity) {
        if(
            (similarity.getR().getRowDimension() !=
                similarity.getR().getColumnDimension()) ||
                similarity.getR().getColumnDimension() != 3
        ) {
            throw new RuntimeException("Use this class with 3D rotations");
        }
        double alpha = atan2(similarity.getR().get(1, 0), similarity.getR().get(0, 0));
        double beta = atan2(-1 * similarity.getR().get(2, 0), Math.sqrt(Math.pow(similarity.getR().get(0, 0), 2) + Math.pow(similarity.getR().get(1, 0), 2)));
        double gamma = atan2(similarity.getR().get(2, 1), similarity.getR().get(2, 2));
        return new double[] {
            alpha,
            beta,
            gamma
        };
    }

    public double[] getZYZEulerParameters(Similarity similarity) {
        if(
            (similarity.getR().getRowDimension() !=
                similarity.getR().getColumnDimension()) ||
                similarity.getR().getColumnDimension() != 3
        ) {
            throw new RuntimeException("Use this class with 3D rotations");
        }
        Matrix R = similarity.getR();
        double alpha = atan2(R.get(1, 2), R.get(0, 2));
        double gamma = atan2(R.get(2, 1), -1 * R.get(2, 0));
        double beta = atan2(R.get(2, 1), sin(gamma) * R.get(2, 2));

        return new double[] {
            alpha,
            beta,
            gamma
        };
    }
}
