package fr.univ_nantes.ec_clem.test.storage;

import Jama.Matrix;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import plugins.perrine.ec_clem.ec_clem.storage.transformation.csv.CsvToMatrixFileReader;
import plugins.perrine.ec_clem.ec_clem.storage.transformation.csv.TransformationToCsvFileWriter;
import plugins.perrine.ec_clem.ec_clem.transformation.AffineTransformation;

import javax.inject.Inject;
import java.io.File;

import static org.testng.Assert.assertEquals;

public class TransformationToCsvTest {

    private File file = new File(String.format("%s.test", getClass().getSimpleName()));

    private TransformationToCsvFileWriter transformationToCsvFileWriter;
    private CsvToMatrixFileReader csvToMatrixFileReader;

    public TransformationToCsvTest() {
        DaggerTransformationToCsvTestComponent.create().inject(this);
    }

    @BeforeTest
    @AfterTest
    private void clearFile() {
        file.delete();
    }

    @Test
    void writeAndRead() {
        Matrix matrix = new Matrix(new double[][]{
            {1, 2},
            {3, 4}
        });
        AffineTransformation transformation = new AffineTransformation(matrix);
        transformationToCsvFileWriter.save(transformation, file);
        Matrix read = csvToMatrixFileReader.read(file);
        for(int i = 0; i < matrix.getRowDimension(); i++) {
            for(int j = 0; j < matrix.getColumnDimension(); j++) {
                assertEquals(transformation.getHomogeneousMatrix().get(i, j), read.get(i, j));
            }
        }
    }

    @Inject
    public void setTransformationToCsvFileWriter(TransformationToCsvFileWriter transformationToCsvFileWriter) {
        this.transformationToCsvFileWriter = transformationToCsvFileWriter;
    }

    @Inject
    public void setCsvToMatrixFileReader(CsvToMatrixFileReader csvToMatrixFileReader) {
        this.csvToMatrixFileReader = csvToMatrixFileReader;
    }
}
