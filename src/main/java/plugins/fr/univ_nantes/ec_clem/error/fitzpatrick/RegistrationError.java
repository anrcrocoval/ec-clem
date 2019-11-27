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
package plugins.fr.univ_nantes.ec_clem.error.fitzpatrick;

public class RegistrationError {

    private double predictedError;
    private double measuredError;
    private double distanceToBarycentre;

    public RegistrationError(double predictedError, double measuredError, double distanceToBarycentre) {
        this.predictedError = predictedError;
        this.measuredError = measuredError;
        this.distanceToBarycentre = distanceToBarycentre;
    }

    public double getPredictedError() {
        return predictedError;
    }

    public void setPredictedError(double predictedError) {
        this.predictedError = predictedError;
    }

    public double getMeasuredError() {
        return measuredError;
    }

    public void setMeasuredError(double measuredError) {
        this.measuredError = measuredError;
    }

    public double getDistanceToBarycentre() {
        return distanceToBarycentre;
    }

    public void setDistanceToBarycentre(double distanceToBarycentre) {
        this.distanceToBarycentre = distanceToBarycentre;
    }
}
