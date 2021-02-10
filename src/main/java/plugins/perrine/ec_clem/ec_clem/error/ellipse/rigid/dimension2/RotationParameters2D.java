package plugins.fr.univ_nantes.ec_clem.ec_clem.error.ellipse.rigid.dimension2;

import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.Similarity;
import javax.inject.Inject;

public class RotationParameters2D {

    @Inject
    public RotationParameters2D() {}

    public double getTheta(Similarity similarity) {
        if(
            (similarity.getR().getRowDimension() !=
                similarity.getR().getColumnDimension()) ||
                similarity.getR().getColumnDimension() != 2
        ) {
            throw new RuntimeException("Use this class with 2D rotations");
        }
        return Math.atan2(similarity.getR().get(1, 0), similarity.getR().get(0, 0));
    }
}
