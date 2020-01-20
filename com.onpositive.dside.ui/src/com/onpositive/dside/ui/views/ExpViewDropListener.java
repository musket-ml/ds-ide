package com.onpositive.dside.ui.views;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import com.onpositive.dside.ui.navigator.ExperimentNode;
import com.onpositive.dside.ui.navigator.ExperimentsNode;
import com.onpositive.musket_core.Experiment;

public class ExpViewDropListener extends ViewerDropAdapter {

	private ExperimentsView experimentsView;

	protected ExpViewDropListener(ExperimentsView experimentsView) {
		super(experimentsView.getViewer());
		this.experimentsView = experimentsView;
	}
	
	 public void dragEnter(DropTargetEvent event) {
		 super.dragEnter(event);
		 for (TransferData data : event.dataTypes) {
			 if (LocalSelectionTransfer.getTransfer().isSupportedType(data) && canDrop(LocalSelectionTransfer.getTransfer().getSelection())) {
				 event.detail = DND.DROP_LINK;
				 return;
			 }
		 }
		 event.detail = DND.DROP_NONE;
	 }

	private boolean canDrop(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection struct = (IStructuredSelection) selection;
			List<?> list = struct.toList();
			for (Object object : list) {
				if (object instanceof ExperimentNode || object instanceof ExperimentsNode) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean performDrop(Object data) {
		if (data instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) data;
			List<?> list = sel.toList();
			List<Experiment> childExperiments = list.stream().filter(obj -> obj instanceof ExperimentsNode)
					.flatMap(obj -> ((ExperimentsNode) obj).getExperiments().stream())
					.filter(obj -> obj instanceof ExperimentNode).map(obj -> ((ExperimentNode) obj).getExperiment())
					.collect(Collectors.toList());
			List<Experiment> experiments = list.stream().filter(obj -> obj instanceof ExperimentNode)
					.map(obj -> ((ExperimentNode) obj).getExperiment()).collect(Collectors.toList());
			experiments.addAll(childExperiments);
			if (!experiments.isEmpty()) {
				experimentsView.addExperiments(experiments);
			}
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;
	}

}
