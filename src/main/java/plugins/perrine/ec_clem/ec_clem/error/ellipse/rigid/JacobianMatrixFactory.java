package plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid;

import plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.dimension2.JacobianMatrix2D;
import plugins.perrine.ec_clem.ec_clem.error.ellipse.rigid.dimension3.JacobianMatrix3D;
import javax.inject.Inject;

public class JacobianMatrixFactory {

    private JacobianMatrix2D jacobianMatrix2D;
    private JacobianMatrix3D jacobianMatrix3D;

    @Inject
    public JacobianMatrixFactory(JacobianMatrix2D jacobianMatrix2D, JacobianMatrix3D jacobianMatrix3D) {
        this.jacobianMatrix2D = jacobianMatrix2D;
        this.jacobianMatrix3D = jacobianMatrix3D;
    }

    public JacobianMatrix getFrom(int dimension) {
        switch (dimension) {
            case 2: return jacobianMatrix2D;
            case 3: return jacobianMatrix3D;
            default: throw new RuntimeException("Unimplemented !");
        }
    }
}
