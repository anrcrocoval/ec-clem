package plugins.fr.univ_nantes.ec_clem.storage.transformation;

import Jama.Matrix;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvToMatrixFileReader {

    @Inject
    public CsvToMatrixFileReader() {}

    public Matrix read(File file) {
        try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.DEFAULT)) {
            List<CSVRecord> records = parser.getRecords();
            Matrix matrix = new Matrix(records.size(), records.get(0).size(), 0);
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                for(int j = 0; j < matrix.getColumnDimension(); j++) {
                    matrix.set(i, j, Double.parseDouble(records.get(i).get(j)));
                }
            }
            return matrix;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
