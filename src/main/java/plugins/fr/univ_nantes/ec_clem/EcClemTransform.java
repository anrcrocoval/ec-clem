package plugins.fr.univ_nantes.ec_clem;

import icy.sequence.Sequence;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.fr.univ_nantes.ec_clem.sequence.SequenceUpdater;
import plugins.fr.univ_nantes.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.fr.univ_nantes.ec_clem.transformation.schema.TransformationSchema;
import javax.inject.Inject;

public class EcClemTransform extends EzPlug implements Block {

    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;
    private EzVarFile inputFiducialFile = new EzVarFile("fiducial file", null);
    private EzVarSequence inputSequence = new EzVarSequence("input sequence");

    public EcClemTransform() {
        DaggerEcClemTransformComponent.builder().build().inject(this);
    }

    @Override
    protected void initialize() {
        addEzComponent(inputFiducialFile);
        addEzComponent(inputSequence);
    }

    @Override
    public void clean() {}

    @Override
    public void declareInput(VarList varList) {
        varList.add("0", inputSequence.getVariable());
        varList.add("1", inputFiducialFile.getVariable());
    }

    @Override
    public void declareOutput(VarList varList) {}

    @Override
    public void execute() {
        TransformationSchema transformationSchema = xmlToTransformationSchemaFileReader.read(inputFiducialFile.getValue());
        Sequence copy = inputSequence.getValue();
        SequenceUpdater sequenceUpdater = new SequenceUpdater(copy, transformationSchema);
        sequenceUpdater.run();
    }

    @Inject
    public void setXmlToTransformationSchemaFileReader(XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader) {
        this.xmlToTransformationSchemaFileReader = xmlToTransformationSchemaFileReader;
    }
}