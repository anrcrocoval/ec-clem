package plugins.perrine.easyclemv0.error;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import plugins.perrine.easyclemv0.model.Dataset;

public class FREComputer {

    private Mean mean = new Mean();

    public double getExpectedSquareFRE(Dataset transformedSourceDataset, Dataset targetDataset) {
        mean.clear();
        for(int i = 0; i < transformedSourceDataset.getN(); i++) {
            mean.increment(
                transformedSourceDataset.getPoint(i).minus(targetDataset.getPoint(i)).getSumOfSquare()
            );
        }
        return mean.getResult();
    }
}
