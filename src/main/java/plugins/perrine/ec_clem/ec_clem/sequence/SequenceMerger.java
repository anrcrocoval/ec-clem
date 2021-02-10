package plugins.perrine.ec_clem.ec_clem.sequence;

import icy.sequence.Sequence;
import javax.inject.Inject;
import java.util.function.Supplier;

public class SequenceMerger implements Supplier<Sequence> {

    private SequenceFactory sequenceFactory;
    private Sequence sourceSequence;
    private Sequence targetSequence;

    public SequenceMerger(Sequence sourceSequence, Sequence targetSequence) {
        DaggerSequenceMergerComponent.builder().build().inject(this);
        this.sourceSequence = sourceSequence;
        this.targetSequence = targetSequence;
    }

    @Override
    public Sequence get() {
        return sequenceFactory.getMergeSequence(sourceSequence, targetSequence);
    }

    @Inject
    public void setSequenceFactory(SequenceFactory sequenceFactory) {
        this.sequenceFactory = sequenceFactory;
    }
}
