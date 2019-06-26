package plugins.perrine.easyclemv0.model;

import Jama.Matrix;

public class Similarity extends AffineTransformation {

    private Matrix scale;
    private double scaleValue;

    public Similarity(Matrix R, Matrix T, Matrix scale) {
        super(scale.times(R), T);
        this.A = R;
        this.scale = scale;
    }

    public Similarity(Matrix R, Matrix T, double scale) {
        this(R, T, Matrix.identity(T.getRowDimension(), T.getRowDimension()).times(scale));
        scaleValue = scale;
    }

//    public Point apply(Point point) {
//        return new Point(R.times(scale).times(point.getmatrix()).plus(T));
//    }
//
//    public Dataset apply(Dataset dataset) {
//        Matrix M = dataset.getMatrix().times(R.times(scale));
//        for(int j = 0; j < M.getColumnDimension(); j++) {
//            for(int i = 0; i < M.getRowDimension(); i++) {
//                M.set(i, j, M.get(i, j) + T.get(j, 0));
//            }
//        }
//        return new Dataset(M.getArray());
//    }

    public Matrix getR() {
        return A;
    }

    public Matrix getT() {
        return T;
    }

    public double getScale() {
        return scaleValue;
    }

//    public Matrix getMatrix() {
//        Matrix S = new Matrix(R.getRowDimension() + 1, R.getColumnDimension() + 1, 0);
//        for(int i = 0; i < R.getRowDimension(); i++) {
//            for(int j = 0; j < R.getColumnDimension(); j++) {
//                S.set(i, j, R.get(i, j) * scale);
//            }
//        }
//        for(int i = 0; i < T.getRowDimension(); i++) {
//            S.set(i, S.getColumnDimension() - 1, T.get(i, 0));
//        }
//        S.set(S.getRowDimension() - 1, S.getColumnDimension() - 1, 1);
//        return S;
//    }

    public void printSummary() {
        switch(A.getColumnDimension()) {
            case 2: printSummary2D();
                break;
            case 3: printSummary3D();
                break;
        }
    }

    private void printSummary2D() {
        double anglexy = Math.atan2(A.get(1, 0), A.get(0, 0));
        anglexy = Math.round(Math.toDegrees(anglexy) * 1000.0) / 1000.0;
        double dxt = Math.round(A.get(3, 0) * 1000.0) / 1000.0;
        double dyt = Math.round(A.get(3, 1) * 1000.0) / 1000.0;
//        scale = Math.round(scaleValue * 1000.0) / 1000.0;
        System.out.println("Total computed Translation x " + dxt + " Total Translation y " + dyt + " angle Oz (in degrees) " + anglexy + " Scale " + scaleValue);
    }

    private void printSummary3D() {
        double angleyz = Math.atan2(A.get(2, 1), A.get(2, 2));
        double anglexz = Math.atan2(-A.get(2, 0), Math.sqrt(A.get(2, 1) * A.get(2, 1) + A.get(2, 2) * A.get(2, 2)));
        double anglexy = Math.atan2(A.get(1, 0), A.get(0, 0));
        angleyz = Math.round(Math.toDegrees(angleyz) * 1000.0) / 1000.0;
        anglexz = Math.round(Math.toDegrees(anglexz) * 1000.0) / 1000.0;
        anglexy = Math.round(Math.toDegrees(anglexy) * 1000.0) / 1000.0;
        double dxt = Math.round(T.get(0, 0) * 1000.0) / 1000.0;
        double dyt = Math.round(T.get(1, 0) * 1000.0) / 1000.0;
        double dzt = Math.round(T.get(2, 0) * 1000.0) / 1000.0;
        System.out.println("Total computed Translation x: " + dxt + "  y:" + dyt + "z: " + dzt
            + " angle Oz: " + anglexy + " angle Oy: " + anglexz + " angle Ox: " + angleyz
            + " Scale xy (in physical unit): " + scaleValue + " Scale z:  " + scaleValue);
    }
}
