package plugins.perrine.easyclemv0.error.fitzpatrick;

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
