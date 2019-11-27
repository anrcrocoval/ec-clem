/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.fr.univ_nantes.ec_clem.sequence_listener;

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
