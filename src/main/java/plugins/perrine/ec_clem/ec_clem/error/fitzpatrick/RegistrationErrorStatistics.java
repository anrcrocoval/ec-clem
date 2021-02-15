/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.ec_clem.ec_clem.error.fitzpatrick;

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
