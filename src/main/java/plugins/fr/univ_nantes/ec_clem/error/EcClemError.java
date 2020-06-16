package plugins.fr.univ_nantes.ec_clem.error;

import icy.gui.viewer.Viewer;
import icy.image.colormap.FireColorMap;
import icy.system.thread.ThreadUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.vars.lang.VarFile;
import plugins.adufour.vars.lang.VarSequence;
import plugins.fr.univ_nantes.ec_clem.ec_clem.error.fitzpatrick.TargetRegistrationErrorMapFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.error.fitzpatrick.TargetRegistrationErrorMapSupplier;
import plugins.fr.univ_nantes.ec_clem.ec_clem.registration.RegistrationParameter;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation.csv.TransformationToCsvFileWriter;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation.csv.TransformationToCsvFormatter;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.RegistrationParameterFactory;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;
import java.io.File;

public class EcClemError extends EzPlug implements Block {

    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;
    private RegistrationParameterFactory registrationParameterFactory;
    private TransformationToCsvFormatter transformationToCsvFormatter;
    private TargetRegistrationErrorMapFactory targetRegistrationErrorMapFactory;
    private TransformationToCsvFileWriter transformationToCsvFileWriter;

    private EzVarFile inputFiducialFile = new EzVarFile("transformation schema file", null);
    private VarFile outputTransformationFile = new VarFile("transformation file", null);
    private VarSequence outputErrorMap = new VarSequence("error map", null);

    public EcClemError() {
        DaggerEcClemErrorComponent.builder().build().inject(this);
    }

    @Override
    public void declareInput(VarList varList) {
        varList.add("0", inputFiducialFile.getVariable());
    }

    @Override
    public void declareOutput(VarList varList) {
        varList.add("1", outputTransformationFile);
        varList.add("2", outputErrorMap);
    }

    @Override
    protected void initialize() {
        addEzComponent(inputFiducialFile);
    }

    @Override
    protected void execute() {
        TransformationSchema transformationSchema = xmlToTransformationSchemaFileReader.read(inputFiducialFile.getValue());

        RegistrationParameter registrationParameter = registrationParameterFactory.getFrom(transformationSchema);
        File outputFile = new File("transformation");
        transformationToCsvFileWriter.save(registrationParameter.getTransformation(), outputFile);
        outputTransformationFile.setValue(outputFile);

        TargetRegistrationErrorMapSupplier targetRegistrationErrorMapSupplier = targetRegistrationErrorMapFactory.getFrom(transformationSchema);
        outputErrorMap.setValue(targetRegistrationErrorMapSupplier.get());

        if(!isHeadLess()) {
            ThreadUtil.invokeLater(() -> {
                Viewer viewer = new Viewer(outputErrorMap.getValue());
                viewer.getLut()
                    .getLutChannel(0)
                    .setColorMap(new FireColorMap(), false);
            });
        }
    }

    @Override
    public void clean() {}

    @Inject
    public void setXmlToTransformationSchemaFileReader(XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader) {
        this.xmlToTransformationSchemaFileReader = xmlToTransformationSchemaFileReader;
    }

    @Inject
    public void setRegistrationParameterFactory(RegistrationParameterFactory registrationParameterFactory) {
        this.registrationParameterFactory = registrationParameterFactory;
    }

    @Inject
    public void setTransformationToCsvFormatter(TransformationToCsvFormatter transformationToCsvFormatter) {
        this.transformationToCsvFormatter = transformationToCsvFormatter;
    }

    @Inject
    public void setTargetRegistrationErrorMapFactory(TargetRegistrationErrorMapFactory targetRegistrationErrorMapFactory) {
        this.targetRegistrationErrorMapFactory = targetRegistrationErrorMapFactory;
    }

    @Inject
    public void setTransformationToCsvFileWriter(TransformationToCsvFileWriter transformationToCsvFileWriter) {
        this.transformationToCsvFileWriter = transformationToCsvFileWriter;
    }
}
