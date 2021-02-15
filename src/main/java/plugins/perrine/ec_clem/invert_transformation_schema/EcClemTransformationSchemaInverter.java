package plugins.perrine.ec_clem.invert_transformation_schema;

import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.perrine.ec_clem.ec_clem.registration.RegistrationParameter;
import plugins.perrine.ec_clem.ec_clem.storage.transformation.xml.TransformationToXmlFileWriter;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.writer.TransformationSchemaToXmlFileWriter;
import plugins.perrine.ec_clem.ec_clem.transformation.RegistrationParameterFactory;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;

import java.io.File;

import javax.inject.Inject;

public class EcClemTransformationSchemaInverter extends EzPlug implements Block {

    private EzVarFile inputTransformationSchema = new EzVarFile("Input transformation schema", null);
    private EzVarFile outputTransformationSchema = new EzVarFile("Output transformation schema", null);

    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;
    private TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter;
    private TransformationToXmlFileWriter transformationToXmlFileWriter;
    private RegistrationParameterFactory registrationParameterFactory;
    
    public EcClemTransformationSchemaInverter() {
        DaggerEcClemTransformationSchemaInverterComponent.builder().build().inject(this);
    }

    @Override
    public void declareInput(VarList varList) {
        varList.add("0", inputTransformationSchema.getVariable());
        varList.add("1", outputTransformationSchema.getVariable());
    }

    @Override
    public void declareOutput(VarList varList) {}

    @Override
    protected void initialize() {
        addEzComponent(inputTransformationSchema);
        addEzComponent(outputTransformationSchema);
    }

    @Override
    protected void execute() {
        TransformationSchema read = xmlToTransformationSchemaFileReader.read(inputTransformationSchema.getValue());
        transformationSchemaToXmlFileWriter.save(read.inverse(), outputTransformationSchema.getValue());
        RegistrationParameter from = registrationParameterFactory.getFrom(read.inverse());
        transformationToXmlFileWriter.save(
                from.getTransformation(),
                read.inverse(),
                new File(outputTransformationSchema.getValue().getPath().replace(".xml", "matrixtransfo.xml"))
            );
    }

    @Override
    public void clean() {}

    @Inject
    public void setXmlToTransformationSchemaFileReader(XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader) {
        this.xmlToTransformationSchemaFileReader = xmlToTransformationSchemaFileReader;
    }

    @Inject
    public void setTransformationSchemaToXmlFileWriter(TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter) {
        this.transformationSchemaToXmlFileWriter = transformationSchemaToXmlFileWriter;
    }
    @Inject
    public void setTransformationToXmlFileWriter(TransformationToXmlFileWriter transformationToXmlFileWriter) {
        this.transformationToXmlFileWriter = transformationToXmlFileWriter;
    }
    @Inject
    public void setRegistrationParameterFactory(RegistrationParameterFactory registrationParameterFactory) {
        this.registrationParameterFactory = registrationParameterFactory;
    }
}
