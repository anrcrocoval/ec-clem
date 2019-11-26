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
package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import java.util.concurrent.*;
import static java.lang.Math.*;

public class BaseOptimProblem {

    private FiducialSet fiducialSet;

    public int getNParameters() {
        return 7;
    }

    public int getNConstraints() {
        return 0;
    }

    public int getNonZeroElementsInConstraintJacobian() {
        return 0;
    }

    public int getNonZeroElementsInParametersHessian() {
        return 28;
    }

    public double[] getStartingPoint() {
        return new double[] {
            0, 0, 0, 1, 0, 0, 1
        };
    }

    private LoadingCache<TmpCacheWrapper1, Double> tmp5Cache = CacheBuilder.newBuilder()
        .maximumSize(1000000)
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .build(new CacheLoader<TmpCacheWrapper1, Double>() {
                   public Double load(TmpCacheWrapper1 key) {
                       return computetmp1BigDecimal(key.getY(), key.getZ(), key.getTheta(), key.getTx());
                   }
               }
        );

    private LoadingCache<TmpCacheWrapper1, Double> tmp6Cache = CacheBuilder.newBuilder()
        .maximumSize(1000000)
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .build(new CacheLoader<TmpCacheWrapper1, Double>() {
                   public Double load(TmpCacheWrapper1 key) {
                       return computetmp2BigDecimal(key.getY(), key.getZ(), key.getTheta(), key.getTx());
                   }
               }
        );

    private LoadingCache<TmpCacheWrapper2, Double> tmp7Cache = CacheBuilder.newBuilder()
        .maximumSize(1000000)
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .build(new CacheLoader<TmpCacheWrapper2, Double>() {
                   public Double load(TmpCacheWrapper2 key) {
                       return computetmp3BigDecimal(key.getZ(), key.getTheta());
                   }
               }
        );

    private LoadingCache<TmpCacheWrapper2, Double> tmp8Cache = CacheBuilder.newBuilder()
        .maximumSize(1000000)
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .build(new CacheLoader<TmpCacheWrapper2, Double>() {
                   public Double load(TmpCacheWrapper2 key) {
                       return computetmp4BigDecimal(key.getZ(), key.getTheta());
                   }
               }
        );

    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private CompletionService<Double> completionService = new ExecutorCompletionService<>(executorService);

    public BaseOptimProblem(FiducialSet fiducialSet) {
        this.fiducialSet = fiducialSet;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    public double getObjectiveValue(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda11 = pow(v11, 2) + (pow(v21, 2));
        double lambda12 = v11 * v12 + v21 * v22;
        double lambda22 = pow(v12, 2) + pow(v22, 2);
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += tmp1*(tmp1*lambda11+tmp2*lambda12)
                + tmp2*(tmp1*lambda12+tmp2*lambda22);
        }
        return (log(detV / (2d * PI)) * fiducialSet.getN() - sum / 2d) * -1d;
    }

    private double getderivative0Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda11 = pow(v11, 2) + (pow(v21, 2));
        double lambda12 = v11 * v12 + v21 * v22;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += tmp1*lambda11+tmp2*lambda12;
        }
        return sum * -1d;
    }

    private double getderivative1Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda12 = v11 * v12 + v21 * v22;
        double lambda22 = pow(v12, 2) + pow(v22, 2);
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += tmp1*lambda12+tmp2*lambda22;
        }
        return sum * -1;
    }

    private double getderivative2Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda11 = pow(v11, 2) + (pow(v21, 2));
        double lambda12 = v11 * v12 + v21 * v22;
        double lambda22 = pow(v12, 2) + pow(v22, 2);
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += tmp1*(tmp3*lambda11+tmp4*lambda12)
                + tmp2*(tmp3*lambda12+tmp4*lambda22);
        }
        return sum;
    }

    private double getderivative3Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*v11*pow(tmp1, 2)
                + 2d*v12*tmp1*tmp2;
        }
        return (fiducialSet.getN() * v22 / detV - sum / 2d) * -1d;
    }

    private double getderivative4Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*v11*tmp1*tmp2
                +2d*v12*pow(tmp2, 2);
        }
        return (fiducialSet.getN()*v21* -1d / detV - sum / 2d) * -1d;
    }

    private double getderivative5Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*v21*pow(tmp1, 2)
                +2d*v22*tmp1*tmp2;
        }
        return (fiducialSet.getN()*v12* -1d / detV - sum / 2d) * -1d;
    }

    private double getderivative6Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*v21*tmp1*tmp2
                +2d*v22*pow(tmp2, 2);
        }
        return (fiducialSet.getN()*v11/detV - sum / 2d) * -1d;
    }

    private double gettmp1BigDecimal(double[] y, double[] z, double theta, double tx) {
        TmpCacheWrapper1 wrap = new TmpCacheWrapper1().setY(y).setZ(z).setTheta(theta).setTx(tx);
        return tmp5Cache.getUnchecked(wrap);
    }

    private double computetmp1BigDecimal(double[] y, double[] z, double theta, double tx) {
        return y[0]
            - z[0] * cos(theta)
            + z[1] * sin(theta)
            - tx;
    }

    private double gettmp2BigDecimal(double[] y, double[] z, double theta, double tx) {
        TmpCacheWrapper1 wrap = new TmpCacheWrapper1().setY(y).setZ(z).setTheta(theta).setTx(tx);
        return tmp6Cache.getUnchecked(wrap);
    }

    private double computetmp2BigDecimal(double[] y, double[] z, double theta, double ty) {
        return y[1]
            - z[0] * sin(theta)
            - z[1] * cos(theta)
            - ty;
    }

    private double gettmp3BigDecimal(double[] z, double theta) {
        TmpCacheWrapper2 wrap = new TmpCacheWrapper2().setZ(z).setTheta(theta);
        return tmp7Cache.getUnchecked(wrap);
    }

    private double computetmp3BigDecimal(double[] z, double theta) {
        return z[0] * sin(theta)
            + z[1] * cos(theta);
    }

    private double gettmp4BigDecimal(double[] z, double theta) {
        TmpCacheWrapper2 wrap = new TmpCacheWrapper2().setZ(z).setTheta(theta);
        return tmp8Cache.getUnchecked(wrap);
    }

    private double computetmp4BigDecimal(double[] z, double theta) {
        return -z[0] * cos(theta)
            + z[1] * sin(theta);
    }

    public double[] getObjectiveGradient(double[] point) throws ExecutionException, InterruptedException {
        Future<Double> submit0 = completionService.submit(() -> getderivative0Value(point));
        Future<Double> submit1 = completionService.submit(() -> getderivative1Value(point));
        Future<Double> submit2 = completionService.submit(() -> getderivative2Value(point));
        Future<Double> submit3 = completionService.submit(() -> getderivative3Value(point));
        Future<Double> submit4 = completionService.submit(() -> getderivative4Value(point));
        Future<Double> submit5 = completionService.submit(() -> getderivative5Value(point));
        Future<Double> submit6 = completionService.submit(() -> getderivative6Value(point));

        return new double[] {
            submit0.get(),
            submit1.get(),
            submit2.get(),
            submit3.get(),
            submit4.get(),
            submit5.get(),
            submit6.get()
        };
    }

    // tx²
    private double getSecondDerivative0Value(double[] point) {
        double v11 = point[3];
        double v21 = point[5];
        double lambda11 = pow(v11, 2) + (pow(v21, 2));

        return fiducialSet.getN()*lambda11;
    }

    // tx*ty
    private double getSecondDerivative1Value(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda12 = v11 * v12 + v21 * v22;

        return fiducialSet.getN()*lambda12;
    }

    // ty²
    private double getSecondDerivative2Value(double[] point) {
        double v12 = point[4];
        double v22 = point[6];
        double lambda22 = pow(v12, 2) + pow(v22, 2);

        return fiducialSet.getN()*lambda22;
    }

    // tx*theta
    private double getSecondDerivative3Value(double[] point) {
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda11 = pow(v11, 2) + (pow(v21, 2));
        double lambda12 = v11 * v12 + v21 * v22;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += tmp3*lambda11+tmp4*lambda12;
        }

        return sum * -1d;
    }

    // ty*theta
    private double getSecondDerivative4Value(double[] point) {
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda12 = v11 * v12 + v21 * v22;
        double lambda22 = pow(v12, 2) + pow(v22, 2);
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += tmp3*lambda12+tmp4*lambda22;
        }

        return sum * -1d;
    }

    // theta²
    private double getSecondDerivative5Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double lambda11 = pow(v11, 2) + (pow(v21, 2));
        double lambda12 = v11 * v12 + v21 * v22;
        double lambda22 = pow(v12, 2) + pow(v22, 2);
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += tmp1*(-tmp4*lambda11+tmp3*lambda12)
                +tmp3*(tmp3*lambda11+tmp4*lambda12)
                +tmp2*(-tmp4*lambda12+tmp3*lambda22)
                +tmp4*(tmp3*lambda12+tmp4*lambda22);
        }

        return sum;
    }

    // tx*v11
    private double getSecondDerivative6Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*v11*tmp1
                +v12*tmp2;
        }

        return sum * -1d;
    }

    // ty*v11
    private double getSecondDerivative7Value(double[] point) {
        double tx = point[0];
        double theta = point[2];
        double v12 = point[4];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            sum += v12*tmp1;
        }

        return sum * -1d;
    }

    // theta*v11
    private double getSecondDerivative8Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += 2d*v11*tmp3*tmp1
                +v12*tmp4*tmp1
                +v12*tmp3*tmp2;
        }

        return sum;
    }

    // v11²
    private double getSecondDerivative9Value(double[] point) {
        double tx = point[0];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            sum += 2d*pow(tmp1, 2);
        }
        return (fiducialSet.getN()*pow(v22, 2)* -1d / pow(detV, 2) - sum / 2d) * -1d;
    }

    // v12*tx
    private double getSecondDerivative10Value(double[] point) {
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += v11*tmp2;
        }
        return sum * -1d;
    }

    // v12*ty
    private double getSecondDerivative11Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += v11*tmp1
                +2d*v12*tmp2;
        }
        return sum * -1d;
    }

    // v12*theta
    private double getSecondDerivative12Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += v11*tmp4*tmp1
                +v11*tmp3*tmp2
                +2d*v12*tmp4*tmp2;
        }
        return sum;
    }

    // v12*v11
    private double getSecondDerivative13Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*tmp1*tmp2;
        }
        return (fiducialSet.getN()*v21*v22 / pow(detV, 2) - sum / 2d) * -1d;
    }

    // v12²
    private double getSecondDerivative14Value(double[] point) {
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*pow(tmp2, 2);
        }
        return (fiducialSet.getN()*pow(v21, 2) * -1d / pow(detV, 2) - sum / 2d) * -1d;
    }

    // v21*tx
    private double getSecondDerivative15Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v21 = point[5];
        double v22 = point[6];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += 2d*v21*tmp1
                +v22*tmp2;
        }
        return sum * -1d;
    }

    // v21*ty
    private double getSecondDerivative16Value(double[] point) {
        double tx = point[0];
        double theta = point[2];
        double v22 = point[6];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            sum += v22*tmp1;
        }
        return sum * -1d;
    }

    //v21*theta
    private double getSecondDerivative17Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v21 = point[5];
        double v22 = point[6];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += 2d*v21*tmp3*tmp1
                +v22*tmp4*tmp1
                +v22*tmp3*tmp2;
        }
        return sum;
    }

    // v21*v11
    private double getSecondDerivative18Value(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;

        return (fiducialSet.getN()*v12*v22 / pow(detV, 2)) * -1d;
    }

    // v21*v12
    private double getSecondDerivative19Value(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;

        return fiducialSet.getN()*v11*v22* -1d / pow(detV, 2) * -1d;
    }

    // v21²
    private double getSecondDerivative20Value(double[] point) {
        double tx = point[0];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            sum +=2d*pow(tmp1, 2);
        }
        return (fiducialSet.getN()*pow(v12, 2) * -1d / pow(detV, 2) - sum / 2d) * -1d;
    }

    // v22*tx
    private double getSecondDerivative21Value(double[] point) {
        double ty = point[1];
        double theta = point[2];
        double v21 = point[5];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += v21*tmp2;
        }
        return sum * -1d;
    }

    // v22*ty
    private double getSecondDerivative22Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v21 = point[5];
        double v22 = point[6];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum += v21*tmp1
                +2d*v22*tmp2;
        }
        return sum * -1d;
    }

    // v22*theta
    private double getSecondDerivative23Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v21 = point[5];
        double v22 = point[6];
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            double tmp3 = gettmp3BigDecimal(z, theta);
            double tmp4 = gettmp4BigDecimal(z, theta);
            sum += v21*tmp4*tmp1
                    +v21*tmp3*tmp2
                    +2d*v22*tmp4*tmp2;
        }
        return sum;
    }

    // v22*v11
    private double getSecondDerivative24Value(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;

        return (fiducialSet.getN()*v12*v21*-1d / pow(detV, 2)) * -1d;
    }

    // v22*v12
    private double getSecondDerivative25Value(double[] point) {
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        return (fiducialSet.getN()*v11*v21 / pow(detV, 2)) * -1d;
    }

    // v22*v21
    private double getSecondDerivative26Value(double[] point) {
        double tx = point[0];
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp1 = gettmp1BigDecimal(y, z, theta, tx);
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum +=2d*tmp1*tmp2;
        }
        return (fiducialSet.getN()*v11*v12 / pow(detV, 2) - sum / 2d) * -1d;
    }

    // v22²
    private double getSecondDerivative27Value(double[] point) {
        double ty = point[1];
        double theta = point[2];
        double v11 = point[3];
        double v12 = point[4];
        double v21 = point[5];
        double v22 = point[6];
        double detV = v11 * v22 - v21 * v12;
        double sum = 0d;
        double[][] target = fiducialSet.getTargetDataset().getMatrix().getArray();
        double[][] source = fiducialSet.getSourceDataset().getMatrix().getArray();
        for(int i = 0; i < fiducialSet.getN(); i++) {
            double[] y = target[i];
            double[] z = source[i];
            double tmp2 = gettmp2BigDecimal(y, z, theta, ty);
            sum +=2d*pow(tmp2, 2);
        }
        return (fiducialSet.getN()*pow(v11, 2) * -1d / pow(detV, 2) - sum / 2d) * -1d;
    }

    public double[] getObjectiveHessian(double[] point) throws ExecutionException, InterruptedException {
        Future<Double> submit0 = completionService.submit(() -> getSecondDerivative0Value(point));
        Future<Double> submit1 = completionService.submit(() -> getSecondDerivative1Value(point));
        Future<Double> submit2 = completionService.submit(() -> getSecondDerivative2Value(point));
        Future<Double> submit3 = completionService.submit(() -> getSecondDerivative3Value(point));
        Future<Double> submit4 = completionService.submit(() -> getSecondDerivative4Value(point));
        Future<Double> submit5 = completionService.submit(() -> getSecondDerivative5Value(point));
        Future<Double> submit6 = completionService.submit(() -> getSecondDerivative6Value(point));
        Future<Double> submit7 = completionService.submit(() -> getSecondDerivative7Value(point));
        Future<Double> submit8 = completionService.submit(() -> getSecondDerivative8Value(point));
        Future<Double> submit9 = completionService.submit(() -> getSecondDerivative9Value(point));
        Future<Double> submit10 = completionService.submit(() -> getSecondDerivative10Value(point));
        Future<Double> submit11 = completionService.submit(() -> getSecondDerivative11Value(point));
        Future<Double> submit12 = completionService.submit(() -> getSecondDerivative12Value(point));
        Future<Double> submit13 = completionService.submit(() -> getSecondDerivative13Value(point));
        Future<Double> submit14 = completionService.submit(() -> getSecondDerivative14Value(point));
        Future<Double> submit15 = completionService.submit(() -> getSecondDerivative15Value(point));
        Future<Double> submit16 = completionService.submit(() -> getSecondDerivative16Value(point));
        Future<Double> submit17 = completionService.submit(() -> getSecondDerivative17Value(point));
        Future<Double> submit18 = completionService.submit(() -> getSecondDerivative18Value(point));
        Future<Double> submit19 = completionService.submit(() -> getSecondDerivative19Value(point));
        Future<Double> submit20 = completionService.submit(() -> getSecondDerivative20Value(point));
        Future<Double> submit21 = completionService.submit(() -> getSecondDerivative21Value(point));
        Future<Double> submit22 = completionService.submit(() -> getSecondDerivative22Value(point));
        Future<Double> submit23 = completionService.submit(() -> getSecondDerivative23Value(point));
        Future<Double> submit24 = completionService.submit(() -> getSecondDerivative24Value(point));
        Future<Double> submit25 = completionService.submit(() -> getSecondDerivative25Value(point));
        Future<Double> submit26 = completionService.submit(() -> getSecondDerivative26Value(point));
        Future<Double> submit27 = completionService.submit(() -> getSecondDerivative27Value(point));

        return new double[] {
            submit0.get(),
            submit1.get(),
            submit2.get(),
            submit3.get(),
            submit4.get(),
            submit5.get(),
            submit6.get(),
            submit7.get(),
            submit8.get(),
            submit9.get(),
            submit10.get(),
            submit11.get(),
            submit12.get(),
            submit13.get(),
            submit14.get(),
            submit15.get(),
            submit16.get(),
            submit17.get(),
            submit18.get(),
            submit19.get(),
            submit20.get(),
            submit21.get(),
            submit22.get(),
            submit23.get(),
            submit24.get(),
            submit25.get(),
            submit26.get(),
            submit27.get(),
        };
    }

    public double[] getConstraints(double[] point) {
        return new double[0];
    }

    public double[] getConstraintsJacobian(double[] point) {
        return new double[0];
    }

    public double[][] getConstraintsHessian(double[] point) {
        return new double[0][0];
    }

    public double[] getParametersLowerBounds() {
        return new double[] {
            -Double.MAX_VALUE,
            -Double.MAX_VALUE,
            -Double.MAX_VALUE,
            -Double.MAX_VALUE,
            -Double.MAX_VALUE,
            -Double.MAX_VALUE,
            -Double.MAX_VALUE
        };
    }

    public double[] getParametersUpperBounds() {
        return new double[] {
            Double.MAX_VALUE,
            Double.MAX_VALUE,
            Double.MAX_VALUE,
            Double.MAX_VALUE,
            Double.MAX_VALUE,
            Double.MAX_VALUE,
            Double.MAX_VALUE
        };
    }

    public double[] getConstraintsLowerBounds() {
        return new double[0];
    }

    public double[] getConstraintsUpperBounds() {
        return new double[0];
    }
}
