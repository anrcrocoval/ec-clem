package plugins.fr.univ_nantes.ec_clem.ec_clem.storage.dataset;

import plugins.fr.univ_nantes.ec_clem.ec_clem.fiducialset.dataset.Dataset;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DatasetToCsvFileWriter {

    private DatasetToCsvFormatter datasetToCsvFormatter;

    @Inject
    public DatasetToCsvFileWriter() {}

    public void save(Dataset dataset, File csvFile) {
        try (FileWriter fileWriter = new FileWriter(csvFile)) {
            fileWriter.write(datasetToCsvFormatter.format(dataset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject
    public void setDatasetToCsvFormatter(DatasetToCsvFormatter datasetToCsvFormatter) {
        this.datasetToCsvFormatter = datasetToCsvFormatter;
    }
}
