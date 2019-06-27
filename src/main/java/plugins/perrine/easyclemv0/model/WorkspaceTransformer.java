package plugins.perrine.easyclemv0.model;

import icy.gui.frame.progress.AnnounceFrame;
import icy.sequence.Sequence;
import plugins.perrine.easyclemv0.image_transformer.SequenceUpdater;

public class WorkspaceTransformer {

    private SequenceUpdater sequenceUpdater = new SequenceUpdater();

    public void apply(Sequence sourceSequence, Transformation transformation) {
        FiducialSet fiducialSet = transformation.getFiducialSet();
        if (fiducialSet.getN() <= fiducialSet.getSourceDataset().getDimension()) {
            System.out.println("One more point");
            new AnnounceFrame("No transformation will be computed with less than " + (fiducialSet.getSourceDataset().getDimension() + 1) + " points. You have placed " + fiducialSet.getN() + " points", 2);
            return;
        }

//        if (fiducialSet.getSourceDataset().isCoplanar() || fiducialSet.getTargetDataset().isCoplanar() || fiducialSet.getN() < 4) {
//            System.out.println("Instability: One more point");
//            new AnnounceFrame("The position of the points does not allow a correct 3D transform. \n You need at least 2 points in separate z (slice). \n You may want to consider a 2D transform (it will still transform the full stack).");
//            return;
//        }

        sequenceUpdater.update(sourceSequence, transformation);
    }

    private void restoreBackup(Sequence sequence, Sequence backup) {
        sequence.setAutoUpdateChannelBounds(false);
        sequence.beginUpdate();
        sequence.removeAllImages();
        try {
            for (int t = 0; t < backup.getSizeT(); t++) {
                for (int z = 0; z < backup.getSizeZ(); z++) {
                    sequence.setImage(t, z, backup.getImage(t, z));
                }
            }
        }
        finally {
            sequence.endUpdate();
        }
    }
}