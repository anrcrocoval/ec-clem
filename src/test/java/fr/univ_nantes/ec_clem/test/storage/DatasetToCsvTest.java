package fr.univ_nantes.ec_clem.test.storage;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.ec_clem.roi.PointType;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.dataset.CsvToDatasetFileReader;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.dataset.DatasetToCsvFileWriter;

import javax.inject.Inject;
import java.io.File;

import static org.testng.Assert.assertEquals;

public class DatasetToCsvTest {

    private File file = new File(String.format("%s.test", getClass().getSimpleName()));

    private DatasetToCsvFileWriter datasetToCsvFileWriter;
    private CsvToDatasetFileReader csvToDatasetFileReader;

    public DatasetToCsvTest() {
        DaggerDatasetToCsvTestComponent.create().inject(this);
    }

    @BeforeTest
    @AfterTest
    private void clearFile() {
        file.delete();
    }

    @Test
    void writeAndRead() {
        Dataset dataset = new Dataset(new double[][] {{ 1, 2, 0 }, { 2, 3, 0}, { 3, 4, 1 }}, PointType.FIDUCIAL);

        datasetToCsvFileWriter.save(dataset, file);
        Dataset read = csvToDatasetFileReader.read(file);

        assertEquals(dataset.getN(), read.getN());
        assertEquals(dataset.getDimension(), read.getDimension());
        for(int i = 0; i < dataset.getN(); i++) {
            for(int j = 0; j < dataset.getDimension(); j++) {
                assertEquals(dataset.getMatrix().get(i, j), read.getMatrix().get(i, j));
            }
        }
    }

    @Inject
    public void setDatasetToCsvFileWriter(DatasetToCsvFileWriter datasetToCsvFileWriter) {
        this.datasetToCsvFileWriter = datasetToCsvFileWriter;
    }

    @Inject
    public void setCsvToDatasetFileReader(CsvToDatasetFileReader csvToDatasetFileReader) {
        this.csvToDatasetFileReader = csvToDatasetFileReader;
    }
}
