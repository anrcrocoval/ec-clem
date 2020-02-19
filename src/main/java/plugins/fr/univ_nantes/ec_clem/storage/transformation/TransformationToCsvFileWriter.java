package plugins.fr.univ_nantes.ec_clem.storage.transformation;

import plugins.fr.univ_nantes.ec_clem.transformation.Transformation;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TransformationToCsvFileWriter {

    private TransformationToCsvFormatter transformationToCsvFormatter;

    @Inject
    public TransformationToCsvFileWriter() {}

    public void save(Transformation transformation, File csvFile) {
        try (FileWriter fileWriter = new FileWriter(csvFile)) {
            fileWriter.write(transformationToCsvFormatter.format(transformation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject
    public void setTransformationToCsvFormatter(TransformationToCsvFormatter transformationToCsvFormatter) {
        this.transformationToCsvFormatter = transformationToCsvFormatter;
    }
}
