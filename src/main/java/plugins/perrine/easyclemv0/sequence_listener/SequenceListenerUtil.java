package plugins.perrine.easyclemv0.sequence_listener;

import icy.sequence.Sequence;
import icy.sequence.SequenceListener;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SequenceListenerUtil {

    @Inject
    public SequenceListenerUtil() {
    }

    public List<SequenceListener> removeListeners(Sequence sequence, Class listenerClass) {
        List<SequenceListener> listeners = Arrays.stream(sequence.getListeners()).filter(
                (listener) -> listenerClass.isAssignableFrom(listener.getClass())
        ).collect(Collectors.toList());
        for(SequenceListener listener : listeners) {
            sequence.removeListener(listener);
        }
        return listeners;
    }

    public void addListeners(Sequence sequence, List<SequenceListener> listeners) {
        for(SequenceListener listener : listeners) {
            sequence.addListener(listener);
        }
    }
}
