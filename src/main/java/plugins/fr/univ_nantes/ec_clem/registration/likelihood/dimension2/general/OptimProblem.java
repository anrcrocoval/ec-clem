package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general;

public interface OptimProblem {
    int getNParameters();
    int getNConstraints();
    int getNonZeroElementsInConstraintJacobian();
    int getNonZeroElementsInParametersHessian();
    double[] getParametersLowerBounds();
    double[] getParametersUpperBounds();
    double[] getConstraintsLowerBounds();
    double[] getConstraintsUpperBounds();
    double[] getStartingPoint();
    double getObjectiveValue(double[] point);
    double[] getObjectiveGradient(double[] point);
    double[] getObjectiveHessian(double[] point);
    double[] getConstraints(double[] point);
    double[] getConstraintsJacobian(double[] point);
    double[][] getConstraintsHessian(double[] point);
    void close();
}
