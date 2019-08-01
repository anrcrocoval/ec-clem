package plugins.perrine.easyclemv0.error;

import javax.inject.Inject;

public class FLEComputer {

    @Inject
    public FLEComputer() {
    }

    public double getExpectedSquareFLE(double expectedSquareFRE, int n) {
        return expectedSquareFRE / (1 - (1 / (2 * (double) n)));
    }
}
