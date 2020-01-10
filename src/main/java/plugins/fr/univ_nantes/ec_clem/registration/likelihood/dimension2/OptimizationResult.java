package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2;

public class OptimizationResult {
    private double[] parameters;
    private double objectiveValue;

    public OptimizationResult(double[] parameters, double objectiveValue) {
        this.parameters = parameters;
        this.objectiveValue = objectiveValue;
    }

    public double[] getParameters() {
        return parameters;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }
}
