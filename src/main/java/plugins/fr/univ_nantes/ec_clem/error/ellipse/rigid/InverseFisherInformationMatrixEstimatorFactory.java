package plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid;

import plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid.dimension2.InverseFisherInformationMatrix2DEstimator;
import plugins.fr.univ_nantes.ec_clem.error.ellipse.rigid.dimension3.InverseFisherInformationMatrix3DEstimator;
import javax.inject.Inject;

public class InverseFisherInformationMatrixEstimatorFactory {

    private InverseFisherInformationMatrix2DEstimator inverseFisherInformationMatrix2DEstimator;
    private InverseFisherInformationMatrix3DEstimator inverseFisherInformationMatrix3DEstimator;

    @Inject
    public InverseFisherInformationMatrixEstimatorFactory(
        InverseFisherInformationMatrix2DEstimator inverseFisherInformationMatrix2DEstimator,
        InverseFisherInformationMatrix3DEstimator inverseFisherInformationMatrix3DEstimator
    ) {
        this.inverseFisherInformationMatrix2DEstimator = inverseFisherInformationMatrix2DEstimator;
        this.inverseFisherInformationMatrix3DEstimator = inverseFisherInformationMatrix3DEstimator;
    }

    public InverseFisherInformationMatrixEstimator getFrom(int dimension) {
        switch (dimension) {
            case 2: return inverseFisherInformationMatrix2DEstimator;
            case 3: return inverseFisherInformationMatrix3DEstimator;
            default: throw new RuntimeException("Unimplemented !");
        }
    }
}
