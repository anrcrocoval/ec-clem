package plugins.perrine.ec_clem.transformation_schema_loader;

import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.perrine.ec_clem.ec_clem.roi.RoiUpdater;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.perrine.ec_clem.ec_clem.roi.RoiUpdater;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;

public class EcClemTransformationSchemaLoader extends EzPlug implements Block {

    private RoiUpdater roiUpdater;
    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;

    private EzVarFile inputFiducialFile = new EzVarFile("transformation schema", null);
    private EzVarSequence inputSourceSequence = new EzVarSequence("source sequence");
    private EzVarSequence inputTargetSequence = new EzVarSequence("target sequence");

    public EcClemTransformationSchemaLoader() {
        DaggerEcClemTransformationSchemaLoaderComponent.builder().build().inject(this);
    }

    @Override
    protected void initialize() {
        addEzComponent(inputSourceSequence);
        addEzComponent(inputTargetSequence);
        addEzComponent(inputFiducialFile);
    }

    @Override
    protected void execute() {
        TransformationSchema transformationSchema = xmlToTransformationSchemaFileReader.read(inputFiducialFile.getValue());
        roiUpdater.updateRoi(transformationSchema.getFiducialSet().getSourceDataset(), inputSourceSequence.getValue());
        roiUpdater.updateRoi(transformationSchema.getFiducialSet().getTargetDataset(), inputTargetSequence.getValue());
    }

    @Override
    public void clean() {}

    @Override
    public void declareInput(VarList varList) {
        varList.add("0", inputSourceSequence.getVariable());
        varList.add("1", inputTargetSequence.getVariable());
        varList.add("2", inputFiducialFile.getVariable());
    }

    @Override
    public void declareOutput(VarList varList) {}

    @Inject
    public void setRoiUpdater(RoiUpdater roiUpdater) {
        this.roiUpdater = roiUpdater;
    }

    @Inject
    public void setXmlToTransformationSchemaFileReader(XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader) {
        this.xmlToTransformationSchemaFileReader = xmlToTransformationSchemaFileReader;
    }
}
