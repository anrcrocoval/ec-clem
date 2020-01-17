package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;

import javax.inject.Inject;

public class RotationParameters3D {

    @Inject
    public RotationParameters3D() {}

    // EulerZYXParameters
    public double[] getAlphaBetaGamma(Similarity similarity) {
        if(
            (similarity.getR().getRowDimension() !=
                similarity.getR().getColumnDimension()) ||
                similarity.getR().getColumnDimension() != 3
        ) {
            throw new RuntimeException("Use this class with 3D rotations");
        }
        double alpha = Math.atan2(similarity.getR().get(1, 0), similarity.getR().get(0, 0));
        double beta = Math.atan2(-1 * similarity.getR().get(2, 0), Math.sqrt(Math.pow(similarity.getR().get(0, 0), 2) + Math.pow(similarity.getR().get(1, 0), 2)));
        double gamma = Math.atan2(similarity.getR().get(2, 1), similarity.getR().get(2, 2));
        return new double[] {
            alpha,
            beta,
            gamma
        };
    }
}
