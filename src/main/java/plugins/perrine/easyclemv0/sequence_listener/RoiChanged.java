//package plugins.perrine.easyclemv0.sequence_listener;
//
//import icy.sequence.Sequence;
//import icy.sequence.SequenceEvent;
//import icy.sequence.SequenceListener;
//import plugins.perrine.easyclemv0.model.WorkspaceState;
//import plugins.perrine.easyclemv0.model.WorkspaceTransformer;
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
