package plugins.fr.univ_nantes.ec_clem.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.point.Point;
import javax.inject.Inject;
import java.io.IOException;

public class DatasetToCsvFormatter implements Formatter<Dataset> {

    @Inject
    public DatasetToCsvFormatter() {}

    public String format(Dataset dataset) {
        StringBuilder stringBuilder = new StringBuilder();
        try (CSVPrinter printer = new CSVPrinter(stringBuilder, CSVFormat.DEFAULT)) {
            printer.printRecord(getHeader(dataset.getDimension()));
            for(int i = 0; i < dataset.getN(); i++) {
                Point point = dataset.getPoint(i);
                printer.print(i);
                for(int j = 0; j < point.getDimension(); j++) {
                    printer.print(point.get(j));
                }
                printer.println();
            }
            printer.flush();
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getHeader(int dimension) {
        String[] header = new String[dimension + 1];
        header[0] = "id";
        for(int i = 1; i < header.length; i++) {
            header[i] = String.format("dimension_%d", i);
        }
        return header;
    }
}
