package plugins.perrine.ec_clem.ec_clem.storage.transformation.csv;

import plugins.perrine.ec_clem.ec_clem.storage.Formatter;
import plugins.perrine.ec_clem.ec_clem.transformation.AffineTransformation;
import plugins.perrine.ec_clem.ec_clem.transformation.SplineTransformation;
import plugins.perrine.ec_clem.ec_clem.transformation.Transformation;
import javax.inject.Inject;

public class TransformationToCsvFormatter implements Formatter<Transformation> {

    private MatrixToCsvFormatter matrixToCsvFormatter;

    @Inject
    public TransformationToCsvFormatter(MatrixToCsvFormatter matrixToCsvFormatter) {
        this.matrixToCsvFormatter = matrixToCsvFormatter;
    }

    public String format(Transformation transformation) {
        if(transformation instanceof AffineTransformation) {
            return format((AffineTransformation) transformation);
        }

        if(transformation instanceof SplineTransformation) {
            return format((SplineTransformation) transformation);
        }

        throw new RuntimeException("Missing binding");
    }

    private String format(AffineTransformation transformation) {
        return matrixToCsvFormatter.format(transformation.getHomogeneousMatrix());
    }

    private String format(SplineTransformation transformation) {
        return "all information in the xml, not in csv for now";
    }
}
