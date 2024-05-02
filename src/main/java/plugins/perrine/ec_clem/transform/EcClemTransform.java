package plugins.perrine.ec_clem.transform;

import icy.main.Icy;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarFile;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.vars.lang.VarSequence;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceSize;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceSizeFactory;
import plugins.perrine.ec_clem.ec_clem.sequence.SequenceUpdater;
import plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.reader.XmlToTransformationSchemaFileReader;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchemaFactory;

import javax.inject.Inject;

public class EcClemTransform extends EzPlug implements Block {

    private XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader;
    private EzVarFile inputFiducialFile = new EzVarFile("transformation schema file", null);
    private EzVarSequence inputSequence = new EzVarSequence("input sequence");
    private VarSequence out= new VarSequence("output sequence", null);
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
    public void declareOutput(VarList varList) {
    	varList.add("Transformed Sequence", out);
    }

    @Override
    public void execute() {
        TransformationSchema transformationSchema = xmlToTransformationSchemaFileReader.read(inputFiducialFile.getValue());
        Sequence copy = null;
		try {
			copy = SequenceUtil.getCopy(inputSequence.getValue(), true, true, true);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Icy.getMainInterface().addSequence(copy);
        if (copy.getSizeX()!=transformationSchema.getSourceSize().get(DimensionId.X).getSize())
        {
        	 throw new RuntimeException("Source size different from the one in the transformation schema");
        }
        if (copy.getPixelSizeX()!=transformationSchema.getSourceSize().get(DimensionId.X).getPixelSizeInMicrometer())
        {
        	 throw new RuntimeException("Pixel Size in micrometers is different from the ones stored in the transformation schema.\n Check the metadata of your image.");
        }
        
        if (copy.getSizeZ()!=transformationSchema.getSourceSize().get(DimensionId.Z).getSize())
        {
        	 SequenceSizeFactory newtargetSizefactory=new SequenceSizeFactory();
        	 SequenceSize newtargetsize=newtargetSizefactory.getFrom(copy);
        	 newtargetsize=newtargetSizefactory.getFrom(transformationSchema.getTargetSize().get(DimensionId.X),transformationSchema.getTargetSize().get(DimensionId.Y),newtargetsize.get(DimensionId.Z));
        	 transformationSchema.setTargetSize(newtargetsize);
        	 
        	 
        }
        
        SequenceUpdater sequenceUpdater = new SequenceUpdater(copy, transformationSchema);
        sequenceUpdater.run();
        out.setValue(copy);
    }

    @Inject
    public void setXmlToTransformationSchemaFileReader(XmlToTransformationSchemaFileReader xmlToTransformationSchemaFileReader) {
        this.xmlToTransformationSchemaFileReader = xmlToTransformationSchemaFileReader;
    }
}