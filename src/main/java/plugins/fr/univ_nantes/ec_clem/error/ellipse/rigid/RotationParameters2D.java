package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import plugins.fr.univ_nantes.ec_clem.transformation.Similarity;

public class RotationParameters2D {

    private double theta;

    public RotationParameters2D(Similarity similarity) {
        if(
            (similarity.getR().getRowDimension() !=
            similarity.getR().getColumnDimension()) ||
            similarity.getR().getColumnDimension() != 2
        ) {
            throw new RuntimeException("Use this class with 2D rotations");
        }
        theta = Math.atan2(similarity.getR().get(1, 0), similarity.getR().get(0, 0));
    }

    public double getTheta() {
        return theta;
    }
}
