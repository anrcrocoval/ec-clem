package plugins.fr.univ_nantes.ec_clem.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.PointFactory;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class CsvToDatasetFileReader {

    private PointFactory pointFactory;

    @Inject
    public CsvToDatasetFileReader(PointFactory pointFactory) {
        this.pointFactory = pointFactory;
    }

    public Dataset read(File file) {
        try (CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.DEFAULT)) {
            Iterator<CSVRecord> iterator = parser.iterator();
            CSVRecord first = iterator.next();
            Dataset dataset = new Dataset(first.size() - 1, PointType.FIDUCIAL);
            double[] coordinates = new double[dataset.getDimension()];
            while (iterator.hasNext()) {
                CSVRecord next = iterator.next();
                for(int i = 0; i < coordinates.length; i++) {
                    coordinates[i] = Double.parseDouble(next.get(i + 1));
                }
                dataset.addPoint(pointFactory.getFrom(coordinates));
            }
            return dataset;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
