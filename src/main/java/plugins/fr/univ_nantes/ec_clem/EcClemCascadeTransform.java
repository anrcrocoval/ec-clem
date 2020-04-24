package plugins.fr.univ_nantes.ec_clem;

import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarText;
import plugins.fr.univ_nantes.ec_clem.fiducialset.FiducialSet;
import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.registration.RegistrationParameter;
import plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.writer.TransformationSchemaToXmlFileWriter;
import plugins.fr.univ_nantes.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.NoiseModel;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationType;

import javax.inject.Inject;
import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EcClemCascadeTransform extends EzPlug {

    private enum KeepType {
        SOURCE_DATASET_FROM_FIRST,
        TARGET_DATASET_FROM_LAST;

        public static String[] toArray() {
            List<String> collect = Arrays.stream(KeepType.values()).map(Enum::name).collect(Collectors.toList());
            String[] array = new String[collect.size()];
            for(int i = 0; i < collect.size(); i++) {
                array[i] = collect.get(i);
            }
            return array;
        }
    }

    private FileList fileList = new FileList();
    private EzVarText keepSelection = new EzVarText(
            "Keep:",
            KeepType.toArray(),
            0, false
    );

    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;
    private TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter;
    private RegistrationParameterFactory registrationParameterFactory;

    public EcClemCascadeTransform() {
        DaggerEcClemCascadeTransformComponent.builder().build().inject(this);
    }

    @Override
    protected void initialize() {
        addComponent(fileList);
        addEzComponent(keepSelection);
    }

    @Override
    protected void execute() {
        fileList.setEnabled(false);
        keepSelection.setEnabled(false);
        List<File> files = fileList.getFiles();
        List<TransformationSchema> schemas = getTransformationSchemaList(files);

        TransformationSchema result = null;
        if(KeepType.valueOf(keepSelection.getValue()).equals(KeepType.TARGET_DATASET_FROM_LAST)) {
            List<TransformationSchema> inversed = new LinkedList<>();
            for(TransformationSchema schema : schemas) {
                inversed.add(0, schema.inverse());
            }
            TransformationSchema newTransformationSchema = getNewTransformationSchema(inversed);
            result = newTransformationSchema.inverse();
        } else {
            result = getNewTransformationSchema(schemas);
        }
        JFileChooser jFileChooser = new JFileChooser();
        int r = jFileChooser.showSaveDialog(null);
        if(r == JFileChooser.APPROVE_OPTION) {
            transformationSchemaToXmlFileWriter.save(result, jFileChooser.getSelectedFile());
        }

        fileList.setEnabled(true);
        keepSelection.setEnabled(true);
    }

    private TransformationSchema getNewTransformationSchema(List<TransformationSchema> schemas) {
        TransformationType transformationType = getTransformationType(schemas);
        NoiseModel noiseModel = getNoiseModel(schemas);
        Dataset transformedDataset = schemas.get(0).getFiducialSet().getSourceDataset();
        for(int i = 0; i < schemas.size(); i++) {
            RegistrationParameter from = registrationParameterFactory.getFrom(schemas.get(i));
            transformedDataset = from.getTransformation().apply(transformedDataset);
        }
        return new TransformationSchema(
            new FiducialSet(schemas.get(0).getFiducialSet().getSourceDataset(), transformedDataset),
            transformationType,
            noiseModel,
            schemas.get(0).getSourceSize(),
            schemas.get(schemas.size() - 1).getTargetSize()
        );
    }

    private List<TransformationSchema> getTransformationSchemaList(List<File> files) {
        List<TransformationSchema> list = new LinkedList<>();
        for(File file : files) {
            list.add(xmlToTransformationSchemaFileReader.read(file));
        }
        return list;
    }

    private TransformationType getTransformationType(List<TransformationSchema> schemas) {
        TransformationType type = TransformationType.RIGID;
        for(TransformationSchema schema : schemas) {
            switch (schema.getTransformationType()) {
                case RIGID: {
                    break;
                }
                case SIMILARITY: {
                    if(type == TransformationType.RIGID) {
                        type = TransformationType.SIMILARITY;
                    }
                    break;
                }
                case AFFINE: {
                    if(type == TransformationType.RIGID || type == TransformationType.SIMILARITY) {
                        type = TransformationType.AFFINE;
                    }
                    break;
                }
                case SPLINE: {
                    return TransformationType.SPLINE;
                }
                default: throw new RuntimeException("Unsupported type");
            }
        }
        return type;
    }

    private NoiseModel getNoiseModel(List<TransformationSchema> schemas) {
        NoiseModel noiseModel = NoiseModel.ISOTROPIC;
        for(TransformationSchema schema : schemas) {
            switch (schema.getNoiseModel()) {
                case ISOTROPIC: {
                    break;
                }
                case ANISOTROPIC: {
                    return NoiseModel.ANISOTROPIC;
                }
                default: throw new RuntimeException("Unsupported type");
            }
        }
        return noiseModel;
    }

    @Override
    public void clean() {

    }

    @Inject
    public void setXmlToTransformationSchemaFileReader(XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader) {
        this.xmlToTransformationSchemaFileReader = xmlToTransformationSchemaFileReader;
    }

    @Inject
    public void setTransformationSchemaToXmlFileWriter(TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter) {
        this.transformationSchemaToXmlFileWriter = transformationSchemaToXmlFileWriter;
    }

    @Inject
    public void setRegistrationParameterFactory(RegistrationParameterFactory registrationParameterFactory) {
        this.registrationParameterFactory = registrationParameterFactory;
    }
}
