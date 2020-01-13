package plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2;

import org.coinor.Ipopt;
import plugins.fr.univ_nantes.ec_clem.registration.likelihood.dimension2.general.OptimProblem;

public class IpoptSolver extends Ipopt {

    private OptimProblem optimProblem;

    public IpoptSolver(OptimProblem optimProblem) {
        this.optimProblem = optimProblem;
        create(
            optimProblem.getNParameters(),
            optimProblem.getNConstraints(),
            optimProblem.getNonZeroElementsInConstraintJacobian(),
            optimProblem.getNonZeroElementsInParametersHessian(),
            Ipopt.C_STYLE
        );
        this.setIntegerOption("max_iter", 5000);
        this.setNumericOption("tol", 1e-25);
        this.setNumericOption("acceptable_tol", 1e-25);
        this.setNumericOption("print_frequency_time", 0);
        this.setIntegerOption("print_frequency_iter", 1);
        this.setIntegerOption("print_level", 0);
        this.setStringOption("linear_solver", "ma27");
    }

    @Override
    protected boolean get_bounds_info(int n, double[] x_L, double[] x_U, int m, double[] g_L, double[] g_U) {
        double[] parametersLowerBounds = optimProblem.getParametersLowerBounds();
        double[] parametersUpperBounds = optimProblem.getParametersUpperBounds();
        double[] constraintsLowerBounds = optimProblem.getConstraintsLowerBounds();
        double[] constraintsUpperBounds = optimProblem.getConstraintsUpperBounds();
        System.arraycopy(parametersLowerBounds, 0, x_L, 0, parametersLowerBounds.length);
        System.arraycopy(parametersUpperBounds, 0, x_U, 0, parametersUpperBounds.length);
        System.arraycopy(constraintsLowerBounds, 0, g_L, 0, constraintsLowerBounds.length);
        System.arraycopy(constraintsUpperBounds, 0, g_U, 0, constraintsUpperBounds.length);
        return true;
    }

    @Override
    protected boolean get_starting_point(int n, boolean init_x, double[] x, boolean init_z, double[] z_L, double[] z_U, int m, boolean init_lambda, double[] lambda) {
        if(init_x) {
            double[] startingPoint = optimProblem.getStartingPoint();
            System.arraycopy(startingPoint, 0, x, 0, startingPoint.length);
        }
        return true;
    }

    @Override
    protected boolean eval_f(int n, double[] x, boolean new_x, double[] obj_value) {
        obj_value[0] = optimProblem.getObjectiveValue(x);
        return true;
    }

    @Override
    protected boolean eval_grad_f(int n, double[] x, boolean new_x, double[] grad_f) {
//        try {
            double[] gradient = optimProblem.getObjectiveGradient(x);
            System.arraycopy(gradient, 0, grad_f, 0, gradient.length);
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
        return true;
    }

    @Override
    protected boolean eval_g(int n, double[] x, boolean new_x, int m, double[] g) {
        double[] constraints = optimProblem.getConstraints(x);
        System.arraycopy(constraints, 0, g, 0, constraints.length);
        return true;
    }

    @Override
    protected boolean eval_jac_g(int n, double[] x, boolean new_x, int m, int nele_jac, int[] iRow, int[] jCol, double[] values) {
        if(values == null) {
            int idx = 0;
            for(int row = 0; row < m; row++) {
                for(int col = 0; col < n; col++) {
                    iRow[idx] = row;
                    jCol[idx] = col;
                    idx++;
                }
            }
        } else {
            double[] constraintsJacobian = optimProblem.getConstraintsJacobian(x);
            System.arraycopy(constraintsJacobian, 0, values, 0, constraintsJacobian.length);
        }
        return true;
    }

    @Override
    protected boolean eval_h(int n, double[] x, boolean new_x, double obj_factor, int m, double[] lambda, boolean new_lambda, int nele_hess, int[] iRow, int[] jCol, double[] values) {
        if(values == null) {
            int idx = 0;
            for(int row = 0; row < n; row++) {
                for(int col = 0; col <= row; col++) {
                    iRow[idx] = row;
                    jCol[idx] = col;
                    idx++;
                }
            }

            assert idx == nele_hess;
        } else {
//            try {
                double[] hessian = optimProblem.getObjectiveHessian(x);
                double[][] constraintsHessian = optimProblem.getConstraintsHessian(x);
                for(int i = 0; i < hessian.length; i++) {
                    values[i] = obj_factor * hessian[i];
                    for(int j = 0; j < constraintsHessian.length; j++) {
                        values[i] += lambda[j] * constraintsHessian[j][i];
                    }
                }
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        return true;
    }
}
