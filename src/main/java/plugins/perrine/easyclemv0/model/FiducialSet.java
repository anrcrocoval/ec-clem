package plugins.perrine.easyclemv0.model;

public class FiducialSet {
    private Dataset sourceDataset;
    private Dataset targetDataset;
    private int n;

    public FiducialSet(Dataset sourceDataset, Dataset targetDataset) {
        this.sourceDataset = sourceDataset;
        this.targetDataset = targetDataset;
        if(sourceDataset.getN() != targetDataset.getN()) {
            throw new RuntimeException("sourceDataset and targetDataset do not have the same number of points");
        }
        n = sourceDataset.getN();
    }

    public Dataset getSourceDataset() {
        return sourceDataset;
    }

    public Dataset getTargetDataset() {
        return targetDataset;
    }

    public int getN() {
        return n;
    }
}
