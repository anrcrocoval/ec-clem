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
//package plugins.perrine.easyclemv0.sequence_listener;
//
//import icy.sequence.Sequence;
//import icy.sequence.SequenceEvent;
//import icy.sequence.SequenceListener;
//import WorkspaceState;
//import WorkspaceTransformer;
//import plugins.perrine.easyclemv0.ui.UpdateTransformationButton;
//
//import java.util.concurrent.CompletionService;
//import java.util.concurrent.ExecutorCompletionService;
//import java.util.concurrent.Executors;
//
//public class RoiChanged implements SequenceListener {
//
//    private WorkspaceState workspaceState;
//    private WorkspaceTransformer workspaceTransformer;
////    private CompletionService<Runnable> completionService = new ExecutorCompletionService<>(
////        Executors.newSingleThreadExecutor()
////    );
//
//    private UpdateTransformationButton updateTransformationButton
//
//    public RoiChanged(WorkspaceState workspaceState, WorkspaceTransformer workspaceTransformer) {
//        this.workspaceState = workspaceState;
//        this.workspaceTransformer = workspaceTransformer;
//    }
//
//    @Override
//    public void sequenceChanged(SequenceEvent event) {
//        if (
//            event.getSourceType() != SequenceEvent.SequenceEventSourceType.SEQUENCE_ROI ||
//                event.getType() != SequenceEvent.SequenceEventType.CHANGED
//        ) {
//            return;
//        }
//
//        if (!workspaceState.isFlagReadyToMove()) {
//            return;
//        }
//
//        if (workspaceState.isStopFlag()) {
//            return;
//        }
//
//        if (!workspaceState.isDone()) {
////            SequenceListener[] eventSequenceListeners = removeListeners(event.getSequence());
////            SequenceListener[] sequenceListeners = removeListeners(sequence);
//            workspaceTransformer.run();
////            addListeners(event.getSequence(), eventSequenceListeners);
////            addListeners(sequence, sequenceListeners);
//            workspaceState.setFlagReadyToMove(false);
//            workspaceState.setDone(true);
//        }
//    }
//
////    private SequenceListener[] removeListeners(Sequence sequence) {
////        SequenceListener[] listeners = sequence.getListeners();
////        for(SequenceListener listener : listeners) {
////            sequence.removeListener(listener);
////        }
////        return listeners;
////    }
////
////    private void addListeners(Sequence sequence, SequenceListener[] listeners) {
////        for(SequenceListener listener : listeners) {
////            sequence.addListener(listener);
////        }
////    }
//
//    @Override
//    public void sequenceClosed(Sequence sequence) {
//
//    }
//}
