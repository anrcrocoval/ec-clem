package plugins.perrine.easyclemv0.error;

public class FLEComputer {

    public double getExpectedSquareFLE(double expectedSquareFRE, int n) {
        return expectedSquareFRE / (1 - (1 / (2 * (double) n)));
    }
}
