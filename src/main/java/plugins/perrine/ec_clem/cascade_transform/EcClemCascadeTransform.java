package plugins.perrine.ec_clem.cascade_transform;

import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarText;
import plugins.perrine.ec_clem.ec_clem.registration.RegistrationParameter;
import plugins.perrine.ec_clem.ec_clem.storage.transformation.xml.TransformationToXmlFileWriter;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.writer.TransformationSchemaToXmlFileWriter;
import plugins.perrine.ec_clem.ec_clem.fiducialset.FiducialSet;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.transformation.RegistrationParameterFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.NoiseModel;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationType;
import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EcClemCascadeTransform extends EzPlug implements Block {

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

//    private FileList fileList = new FileList();
    private EzVarFileList fileList = new EzVarFileList("Schema list");
    private EzVarText keepSelection = new EzVarText(
        "Keep:",
        KeepType.toArray(),
        0, false
    );
    private EzVarFile outputFile = new EzVarFile("Output transformation schema", null);

    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;
    private TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter;
    private TransformationToXmlFileWriter transformationToXmlFileWriter;
    private RegistrationParameterFactory registrationParameterFactory;

    public EcClemCascadeTransform() {
        DaggerEcClemCascadeTransformComponent.builder().build().inject(this);
    }

    @Override
    public void declareInput(VarList varList) {
        varList.add("0", fileList.getVariable());
        varList.add("1", keepSelection.getVariable());
        varList.add("2", outputFile.getVariable());
    }

    @Override
    public void declareOutput(VarList varList) {
    }

    @Override
    protected void initialize() {
        addEzComponent(fileList);
        addEzComponent(keepSelection);
        addEzComponent(outputFile);
    }

    @Override
    protected void execute() {
        fileList.setEnabled(false);
        keepSelection.setEnabled(false);
        outputFile.setEnabled(false);

        List<File> files = fileList.getValue();
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
        File correctedoutputFile = outputFile.getValue(true);
        // Check if xml was added to the file name, otherwise add .xml.
        if (!(outputFile.getValue().getPath().endsWith(".xml"))){
        	correctedoutputFile =new File(outputFile.getValue().getPath()+".xml");
        }
      
        transformationSchemaToXmlFileWriter.save(result, correctedoutputFile);
        RegistrationParameter from = registrationParameterFactory.getFrom(result);
        transformationToXmlFileWriter.save(
                from.getTransformation(),
                result,
                new File(correctedoutputFile.getPath().replace(".xml", "matrix.xml"))
            );
        fileList.setEnabled(true);
        keepSelection.setEnabled(true);
        outputFile.setEnabled(true);
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
            schemas.get(schemas.size() - 1).getTargetSize(),
            schemas.get(0).getSourceName(),
            schemas.get(schemas.size() - 1).getTargetName()
        );
    }

    private List<TransformationSchema> getTransformationSchemaList(List<File> files) {
        List<TransformationSchema> list = new LinkedList<>();
        for(File file : files) { //TODO Add some catcher here when not a transfo.xml
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
    
    @Inject
    public void setTransformationToXmlFileWriter(TransformationToXmlFileWriter transformationToXmlFileWriter) {
        this.transformationToXmlFileWriter = transformationToXmlFileWriter;
    }
}
