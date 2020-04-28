package plugins.fr.univ_nantes.ec_clem.invert_transformation_schema;

import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation_schema.writer.TransformationSchemaToXmlFileWriter;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;

public class EcClemTransformationSchemaInverter extends EzPlug implements Block {

    private EzVarFile inputTransformationSchema = new EzVarFile("Input transformation schema", null);
    private EzVarFile outputTransformationSchema = new EzVarFile("Output transformation schema", null);

    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;
    private TransformationSchemaToXmlFileWriter transformationSchemaToXmlFileWriter;

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
}
