package plugins.perrine.easyclemv0.error;

import plugins.perrine.easyclemv0.factory.TREComputerFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.Point;

public class TREChecker {

    private TREComputerFactory treComputerFactory = new TREComputerFactory();

    public boolean check(Dataset sourceDataset, Dataset targetDataset) {
        boolean check = false;

        TREComputer treComputer = treComputerFactory.getFrom(sourceDataset, targetDataset);

        for (int index = 0; index < sourceDataset.getN(); index++) {
            check |= isEmpiricTreGreaterThanPredictedTre(sourceDataset.getPoint(index), targetDataset.getPoint(index), treComputer);
        }

        return check;
    }

    private boolean isEmpiricTreGreaterThanPredictedTre(Point source, Point target, TREComputer treComputer) {
        double error = source.getDistance(target);
        double predictederror = treComputer.getExpectedSquareTRE(target);
        return error > predictederror;
    }
}
