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
package plugins.fr.univ_nantes.ec_clem.matrix;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import javax.inject.Inject;
import java.util.concurrent.*;

public class MatrixUtil {

    private CompletionService<SingularValueDecomposition> executor = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor());

    @Inject
    public MatrixUtil() {}

    public Matrix pseudoInverse(Matrix M) {

        SingularValueDecomposition svd = null;
        try {
            svd = executor.submit(() -> M.svd()).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            M.print(1, 5);
            e.printStackTrace();
        }

//        SingularValueDecomposition svd = M.svd();
        Matrix S = svd.getS();
        for(int i = 0; i < S.getRowDimension(); i++) {
            if(S.get(i,i) != 0) {
                S.set(i, i, 1 / S.get(i, i));
            }
        }
        return svd.getV().times(S).times(svd.getU().transpose());
    }
}
