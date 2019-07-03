/**
 * Copyright 2010-2017 Perrine Paul-Gilloteaux, CNRS.
 * Perrine.Paul-Gilloteaux@univ-nantes.fr
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/

package plugins.perrine.easyclemv0.image_transformer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import Jama.Matrix;
import icy.gui.frame.progress.ProgressFrame;
import icy.image.IcyBufferedImage;
import icy.image.IcyBufferedImageUtil;
import icy.math.Scaler;
import icy.sequence.DimensionId;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.type.DataType;
import plugins.perrine.easyclemv0.factory.DatasetFactory;
import plugins.perrine.easyclemv0.model.*;
import plugins.perrine.easyclemv0.registration.RigidTransformationComputer;
import plugins.perrine.easyclemv0.roi.RoiUpdater;

/**
 *
 *         This class ImageTransformer is part of EasyClem but could be used as
 *         a library. In this beta version, it makes use of Graphics2D This one was created
 *         during Icy coding party, with the help of St�phane and Fabrice 2D!
 *
 *         23/07/2018: update for better range conservation.
 *         tested with 
 *         Unsigned bit (8 bit) : no loss
 *         signed bit (7 bit + one signed bit): some slight loss
 *         unsigned short (16 bit): no loss
 *         signed short (15 bit plus 1 bit sign): no loss
 *         unsigned int: some slight loss
 *         signed int some slight loss
 *         float some slight loss
 *         double :some slight loss
 *
 *         For lossy conversion, rather use ApplytransformationtoROI if you need an accurate measurement. 
 *
 *         MultiChannel Unsigned and signed byte OK
 *         Unsigned short OK
 *
 */
public class ImageTransformer implements ImageTransformerInterface {

    private RigidTransformationComputer rigidTransformationComputer = new RigidTransformationComputer();
    private RoiUpdater roiUpdater = new RoiUpdater();
    private DatasetFactory datasetFactory = new DatasetFactory();

    private AffineTransform transform;
    private BufferedImage image;
    private Sequence sequence;
    private BufferedImage imageDest;
    private DataType oriType;

    public void setSourceSequence(Sequence value) {
        sequence = value;
        oriType = value.getDataType_();
    }

    private void setParameters(Matrix transfo) {
        if (transfo.getRowDimension() != 3) {
            throw new RuntimeException("Use this class for 2D transformation only");
        }

        transform = new AffineTransform(
            transfo.get(0, 0),
            transfo.get(1, 0),
            transfo.get(0, 1),
            transfo.get(1, 1),
            transfo.get(0, 2),
            transfo.get(1, 2)
        );
    }

    public void setTargetSize(Sequence target) {
        imageDest = new BufferedImage(target.getWidth(), target.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public void setTargetSize(SequenceSize sequenceSize) {
        imageDest = new BufferedImage(
            sequenceSize.get(DimensionId.X).getSize(),
            sequenceSize.get(DimensionId.Y).getSize(),
            BufferedImage.TYPE_INT_ARGB
        );
    }

    public void run(FiducialSet fiducialSet) {

        Similarity similarity = rigidTransformationComputer.compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
        setParameters(similarity.getMatrix());

        System.out.println("I will apply transfo now");
        int nbt = sequence.getSizeT();
        int nbz = sequence.getSizeZ();
        Sequence newseq = SequenceUtil.getCopy(sequence);
        sequence.beginUpdate();
        sequence.removeAllImages();
        ProgressFrame progress = new ProgressFrame("Applying the transformation...");
        progress.setLength(nbt*nbz);
        try {
            for (int t = 0; t < nbt; t++) {
                for (int z = 0; z < nbz; z++) {
                    IcyBufferedImage image = transformIcyImage(newseq, t, z);
                    sequence.setImage(t, z, image);
                    progress.setPosition(1*(z+t*nbz));
                }
            }
        } finally {
            sequence.endUpdate();
        }
        progress.close();
        System.out.println("have been aplied");

        Dataset from = datasetFactory.getFrom(sequence);
        Dataset sourceTransformedDataset = similarity.apply(from);
        roiUpdater.updateRoi(sourceTransformedDataset, sequence);
    }

    private IcyBufferedImage transformIcyImage(Sequence seq, int t, int z) {
        int nbChannels = seq.getSizeC();
        IcyBufferedImage imagetobemodified = seq.getImage(t, z);

        IcyBufferedImage imagetobekept = new IcyBufferedImage(
            imageDest.getWidth(),
            imageDest.getHeight(),
            imagetobemodified.getSizeC(),
            imagetobemodified.getDataType_()
        );

        for (int c = 0; c < nbChannels; c++) {
            if (imagetobemodified.getImage(c).getDataType_().getBitSize() == 16) {

                IcyBufferedImage tmp = IcyBufferedImageUtil.convertToType(imagetobemodified.getImage(c), DataType.INT, false);
                tmp.dataChanged();
                image = new BufferedImage(imagetobemodified.getWidth(), imagetobemodified.getHeight(), BufferedImage.TYPE_INT_ARGB);
                if (imagetobemodified.isSignedDataType()) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        for (int y = 0; y < image.getHeight(); y++) {
                            //add numbers to make it positive
                            image.setRGB(x, y, -(tmp.getDataAsInt(x, y, 0) + 32767));//short max
                        }
                    }
                }
                else {
                    for (int x=0;x<image.getWidth();x++) {
                        for (int y = 0; y < image.getHeight(); y++) {
                            //convertshort unsigned
                            image.setRGB(x, y, -tmp.getDataAsInt(x, y, 0));
                        }
                    }
                }
            } else {
                image=IcyBufferedImageUtil.toBufferedImage(imagetobemodified.getImage(c), BufferedImage.TYPE_INT_ARGB);
            }

            imageDest = new BufferedImage(imagetobekept.getWidth(), imagetobekept.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imageDest.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(image, transform, null);
            g2d.dispose();

            if (imagetobemodified.getImage(c).getDataType_().getBitSize()==16){
                if (imagetobemodified.isSignedDataType()){
                    for (int x=0;x<imageDest.getWidth();x++)
                        for (int y=0;y<imageDest.getHeight();y++){
                            imagetobekept.setData(x, y, c, -(imageDest.getRGB(x, y))-32767);//short max
                        }
                }
                else{
                    for (int x=0;x<imageDest.getWidth();x++)
                        for (int y=0;y<imageDest.getHeight();y++){
                            imagetobekept.setData(x, y, c, -imageDest.getRGB(x, y));
                        }

                }
                imagetobekept.dataChanged();

            }
            else{
                IcyBufferedImage icyImage = IcyBufferedImage.createFrom(imageDest);
                if (icyImage.getDataType_()!=oriType) {
                    double boundsDst[] = imagetobemodified.getImage(c).getChannelsGlobalBounds();
                    Scaler scaler= new Scaler(0, 255,boundsDst[0], boundsDst[1], false);
                    final IcyBufferedImage tmp= IcyBufferedImageUtil.convertToType(icyImage, oriType, scaler);
                    tmp.dataChanged();
                    imagetobekept.copyData(tmp, 0, c);
                    imagetobekept.dataChanged();
                } else {
                    final IcyBufferedImage tmp=IcyBufferedImageUtil.getCopy(icyImage);
                    tmp.dataChanged();
                    imagetobekept.copyData(tmp, 0, c);
                    imagetobekept.dataChanged();
                }
            }
        }
        return imagetobekept;
    }
}
