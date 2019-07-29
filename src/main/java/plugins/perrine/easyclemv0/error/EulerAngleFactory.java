package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

public class EulerAngleFactory {

    public double[] getFrom(Matrix M) {
        Rotation rotation = new Rotation(M.getArray(), 0.01);
        return rotation.getAngles(RotationOrder.ZYZ, RotationConvention.VECTOR_OPERATOR);
    }
}
