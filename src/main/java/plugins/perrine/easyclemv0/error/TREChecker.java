package plugins.perrine.easyclemv0.error;

import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;
import plugins.perrine.easyclemv0.model.TransformationSchema;

public class TREChecker {

    private TREComputerFactory treComputerFactory = new TREComputerFactory();

    public boolean check(TransformationSchema transformationSchema) {
        boolean check = false;

        TREComputer treComputer = treComputerFactory.getFrom(transformationSchema);

        for (int index = 0; index < transformationSchema.getFiducialSet().getN(); index++) {
            check |= isEmpiricTreGreaterThanPredictedTre(
                transformationSchema.getFiducialSet().getSourceDataset().getPoint(index),
                transformationSchema.getFiducialSet().getTargetDataset().getPoint(index),
                treComputer
            );
        }

        return check;
    }

    private boolean isEmpiricTreGreaterThanPredictedTre(Point source, Point target, TREComputer treComputer) {
        double error = source.getDistance(target);
        double predictederror = treComputer.getExpectedSquareTRE(target);
        return error > predictederror;
    }
}
