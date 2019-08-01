package plugins.perrine.easyclemv0.error.fitzpatrick;

import java.util.DoubleSummaryStatistics;
import java.util.ArrayList;
import java.util.List;

public class RegistrationErrorStatistics {

    private List<Double> predictedError = new ArrayList<>();
    private List<Double> measuredError = new ArrayList<>();
    private List<Double> distanceToBarycentre = new ArrayList<>();

    public void add(RegistrationError registrationError) {
        predictedError.add(registrationError.getPredictedError());
        measuredError.add(registrationError.getMeasuredError());
        distanceToBarycentre.add(registrationError.getDistanceToBarycentre());
    }

    public List<Double> getPredictedError() {
        return predictedError;
    }

    public List<Double> getMeasuredError() {
        return measuredError;
    }

    public List<Double> getDistanceToBarycentre() {
        return distanceToBarycentre;
    }

    public DoubleSummaryStatistics getPredictedErrorStatistic() {
        return predictedError.stream().mapToDouble((x) -> x).summaryStatistics();
    }

    public DoubleSummaryStatistics getMeasuredErrorStatistic() {
        return measuredError.stream().mapToDouble((x) -> x).summaryStatistics();
    }

    public DoubleSummaryStatistics getDistanceToBarycentreStatistic() {
        return distanceToBarycentre.stream().mapToDouble((x) -> x).summaryStatistics();
    }
}
