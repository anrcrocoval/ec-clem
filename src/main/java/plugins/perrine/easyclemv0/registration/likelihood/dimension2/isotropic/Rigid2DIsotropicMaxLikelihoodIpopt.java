package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.coinor.Ipopt;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.CacheWrapper;

import java.util.concurrent.ExecutionException;

public class Rigid2DIsotropicMaxLikelihoodIpopt extends Ipopt {

    private FiducialSet fiducialSet;
    private int n;
    private int m;
    private int nele_jac;
    private int nele_hess;

    private LoadingCache<CacheWrapper, DerivativeStructure> cache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .build(new CacheLoader<CacheWrapper, DerivativeStructure>() {
                   public DerivativeStructure load(CacheWrapper key) {
                       return getDerivativeStructure(key.getPoint());
                   }
               }
        );

    private CacheWrapper cacheWrapper = new CacheWrapper();

    public Rigid2DIsotropicMaxLikelihoodIpopt(FiducialSet fiducialSet) {
        this.fiducialSet = fiducialSet;
        nele_jac = 14;
        nele_hess = 28;
        n = 7;
        m = 2;
        create(n, m, nele_jac, nele_hess, Ipopt.C_STYLE);
    }

    @Override
    protected boolean get_bounds_info(int n, double[] x_L, double[] x_U, int m, double[] g_L, double[] g_U) {
        assert n == this.n;
        assert m == this.m;
        x_L[0] = -Double.MAX_VALUE;
        x_U[0] = Double.MAX_VALUE;
        x_L[1] = -Double.MAX_VALUE;
        x_U[1] = Double.MAX_VALUE;
        x_L[2] = 0;
        x_U[2] = 2 * Math.PI;
        x_L[3] = -Double.MAX_VALUE;
        x_U[3] = Double.MAX_VALUE;
        x_L[4] = -Double.MAX_VALUE;
        x_U[4] = Double.MAX_VALUE;
        x_L[5] = -Double.MAX_VALUE;
        x_U[5] = Double.MAX_VALUE;
        x_L[6] = -Double.MAX_VALUE;
        x_U[6] = Double.MAX_VALUE;
        g_L[0] = 0;
        g_U[0] = 0;
        g_L[1] = 0;
        g_U[1] = 0;
        return true;
    }

    @Override
    protected boolean get_starting_point(int n, boolean init_x, double[] x, boolean init_z, double[] z_L, double[] z_U, int m, boolean init_lambda,double[] lambda) {
        if(init_x) {
            x[0] = 0;
            x[1] = 0;
            x[2] = 0;
            x[3] = 1;
            x[4] = 0;
            x[5] = 0;
            x[6] = 1;
        }
        return true;
    }

    @Override
    protected boolean eval_f(int n, double[] x, boolean new_x, double[] obj_value) {
        try {
            obj_value[0] = cache.get(cacheWrapper.set(x)).getValue();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected boolean eval_grad_f(int n, double[] x, boolean new_x, double[] grad_f) {
        try {
            DerivativeStructure derivativeStructure = cache.get(cacheWrapper.set(x));
            grad_f[0] = derivativeStructure.getPartialDerivative(1, 0, 0, 0, 0, 0, 0);
            grad_f[1] = derivativeStructure.getPartialDerivative(0, 1, 0, 0, 0, 0, 0);
            grad_f[2] = derivativeStructure.getPartialDerivative(0, 0, 1, 0, 0, 0, 0);
            grad_f[3] = derivativeStructure.getPartialDerivative(0, 0, 0, 1, 0, 0, 0);
            grad_f[4] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 1, 0, 0);
            grad_f[5] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 0, 1, 0);
            grad_f[6] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 0, 0, 1);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected boolean eval_g(int n, double[] x, boolean new_x, int m, double[] g) {
        g[0] = Math.pow(x[3], 2) + Math.pow(x[5], 2) - Math.pow(x[4], 2) - Math.pow(x[6], 2);
        g[1] = (x[3] * x[4]) + (x[6] * x[5]);
        return true;
    }

    @Override
    protected boolean eval_jac_g(int n, double[] x, boolean new_x, int m, int nele_jac, int[] iRow, int[] jCol, double[] values) {
        assert n == this.n;
        assert m == this.m;

        if(values == null) {
            iRow[0] = 0;  jCol[0] = 0;
            iRow[1] = 0;  jCol[1] = 1;
            iRow[2] = 0;  jCol[2] = 2;
            iRow[3] = 0;  jCol[3] = 3;
            iRow[4] = 0;  jCol[4] = 4;
            iRow[5] = 0;  jCol[5] = 5;
            iRow[6] = 0;  jCol[6] = 6;
            iRow[7] = 1;  jCol[7] = 0;
            iRow[8] = 1;  jCol[8] = 1;
            iRow[9] = 1;  jCol[9] = 2;
            iRow[10] = 1;  jCol[10] = 3;
            iRow[11] = 1;  jCol[11] = 4;
            iRow[12] = 1;  jCol[12] = 5;
            iRow[13] = 1;  jCol[13] = 6;
        } else {
            values[0] = 0;
            values[1] = 0;
            values[2] = 0;
            values[3] = 2 * x[3];
            values[4] = -2 * x[4];
            values[5] = 2 * x[5];
            values[6] = -2 * x[6];
            values[7] = 0;
            values[8] = 0;
            values[9] = 0;
            values[10] = x[4];
            values[11] = x[3];
            values[12] = -x[6];
            values[13] = -x[5];
        }
        return true;
    }

    @Override
    protected boolean eval_h(int n, double[] x, boolean new_x, double obj_factor, int m, double[] lambda, boolean new_lambda, int nele_hess, int[] iRow, int[] jCol, double[] values) {
        assert n == this.n;
        assert m == this.m;

        int idx = 0;
        int row = 0;
        int col = 0;
        if(values == null) {
            idx = 0;
            for(row = 0; row < n; row++) {
                for(col = 0; col <= row; col++) {
                    iRow[idx] = row;
                    jCol[idx] = col;
                    idx++;
                }
            }

            assert idx == nele_hess;
            assert nele_hess == this.nele_hess;
        } else {
            try {
                DerivativeStructure derivativeStructure = cache.get(cacheWrapper.set(x));
                values[0] = derivativeStructure.getPartialDerivative(2, 0, 0, 0, 0, 0, 0) * obj_factor;
                values[1] = derivativeStructure.getPartialDerivative(1, 1, 0, 0, 0, 0, 0) * obj_factor;
                values[2] = derivativeStructure.getPartialDerivative(0, 2, 0, 0, 0, 0, 0) * obj_factor;
                values[3] = derivativeStructure.getPartialDerivative(1, 0, 1, 0, 0, 0, 0) * obj_factor;
                values[4] = derivativeStructure.getPartialDerivative(0, 1, 1, 0, 0, 0, 0) * obj_factor;
                values[5] = derivativeStructure.getPartialDerivative(0, 0, 2, 0, 0, 0, 0) * obj_factor;
                values[6] = derivativeStructure.getPartialDerivative(1, 0, 0, 1, 0, 0, 0) * obj_factor;
                values[7] = derivativeStructure.getPartialDerivative(0, 1, 0, 1, 0, 0, 0) * obj_factor;
                values[8] = derivativeStructure.getPartialDerivative(0, 0, 1, 1, 0, 0, 0) * obj_factor;
                values[9] = derivativeStructure.getPartialDerivative(0, 0, 0, 2, 0, 0, 0) * obj_factor;
                values[10] = derivativeStructure.getPartialDerivative(1, 0, 0, 0, 1, 0, 0) * obj_factor;
                values[11] = derivativeStructure.getPartialDerivative(0, 1, 0, 0, 1, 0, 0) * obj_factor;
                values[12] = derivativeStructure.getPartialDerivative(0, 0, 1, 0, 1, 0, 0) * obj_factor;
                values[13] = derivativeStructure.getPartialDerivative(0, 0, 0, 1, 1, 0, 0) * obj_factor;
                values[14] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 2, 0, 0) * obj_factor;
                values[15] = derivativeStructure.getPartialDerivative(1, 0, 0, 0, 0, 1, 0) * obj_factor;
                values[16] = derivativeStructure.getPartialDerivative(0, 1, 0, 0, 0, 1, 0) * obj_factor;
                values[17] = derivativeStructure.getPartialDerivative(0, 0, 1, 0, 0, 1, 0) * obj_factor;
                values[18] = derivativeStructure.getPartialDerivative(0, 0, 0, 1, 0, 1, 0) * obj_factor;
                values[19] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 1, 1, 0) * obj_factor;
                values[20] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 0, 2, 0) * obj_factor;
                values[21] = derivativeStructure.getPartialDerivative(1, 0, 0, 0, 0, 0, 1) * obj_factor;
                values[22] = derivativeStructure.getPartialDerivative(0, 1, 0, 0, 0, 0, 1) * obj_factor;
                values[23] = derivativeStructure.getPartialDerivative(0, 0, 1, 0, 0, 0, 1) * obj_factor;
                values[24] = derivativeStructure.getPartialDerivative(0, 0, 0, 1, 0, 0, 1) * obj_factor;
                values[25] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 1, 0, 1) * obj_factor;
                values[26] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 0, 1, 1) * obj_factor;
                values[27] = derivativeStructure.getPartialDerivative(0, 0, 0, 0, 0, 0, 2) * obj_factor;

                values[9] += 2 * lambda[0];
                values[14] += -2 * lambda[0];
                values[20] += 2 * lambda[0];
                values[27] += -2 * lambda[0];

                values[13] += lambda[1];
                values[26] +=  -lambda[1];
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private DerivativeStructure getDerivativeStructure(double[] point) {
        DerivativeStructure tx = new DerivativeStructure(7, 2, 0, point[0]);
        DerivativeStructure ty = new DerivativeStructure(7, 2, 1, point[1]);
        DerivativeStructure theta = new DerivativeStructure(7, 2, 2, point[2]);

        DerivativeStructure v_11 = new DerivativeStructure(7, 2, 3, point[3]);
        DerivativeStructure v_12 = new DerivativeStructure(7, 2, 4, point[4]);
        DerivativeStructure v_21 = new DerivativeStructure(7, 2, 5, point[5]);
        DerivativeStructure v_22 = new DerivativeStructure(7, 2, 6, point[6]);

        DerivativeStructure lambda_11 = (v_11.pow(2)).add(v_21.pow(2));

        DerivativeStructure det_lambda = (lambda_11.pow(2));
        DerivativeStructure sum = null;

        for(int i = 0; i < fiducialSet.getN(); i++) {
            Point y = fiducialSet.getTargetDataset().getPoint(i);
            Point z = fiducialSet.getSourceDataset().getPoint(i);
            DerivativeStructure tmp1 = tx.negate()
                .add(y.get(0))
                .subtract(theta.cos().multiply(z.get(0)))
                .add(theta.sin().multiply(z.get(1)));
            DerivativeStructure tmp2 = ty.negate()
                .add(y.get(1))
                .subtract(theta.sin().multiply(z.get(0)))
                .subtract(theta.cos().multiply(z.get(1)));
            DerivativeStructure sum_i = tmp1.multiply(tmp1.multiply(lambda_11))
                .add(tmp2.multiply(tmp2.multiply(lambda_11)));

            if(sum == null) {
                sum = sum_i;
            } else {
                sum.add(sum_i);
            }
        }

        return (((((((det_lambda.reciprocal()).sqrt()).multiply(2 * Math.PI)).reciprocal()).log()).multiply(fiducialSet.getN())).subtract(sum.divide(2))).multiply(-1);
    }
}
