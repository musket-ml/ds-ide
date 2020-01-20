package com.onpositive.dside.ui.editors.preview.augmentation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;

import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.StandaloneDataSetGallery;
import com.onpositive.dside.ui.editors.preview.MusketPreviewEditorPart;

public class AugmentationsPreviewEditorPart extends MusketPreviewEditorPart {
	
	public static final String ID = "com.onpositive.dside.ui.editors.augmentation.preview";

	private StandaloneDataSetGallery dataSetGallery;
	private Composite hostComposite;

	public AugmentationsPreviewEditorPart() {
		setSourceViewerConfiguration(new AugmentationsSourceViewerConfiguration(this));
	}
	
	@Override
	protected void createPreviewControl(Composite parent) {
		hostComposite = new Composite(parent, SWT.NONE);
		hostComposite.setLayout(new FillLayout());
		Label waitLbl = new Label(hostComposite, SWT.NONE);
		waitLbl.setText("Preview is unavailable yet");
	}

	@Override
	protected void doRefreshPreview(IAnalizeResults results) {
		if (dataSetGallery == null) {
			for (Control child : hostComposite.getChildren()) {
				child.dispose();
			}
			dataSetGallery = new StandaloneDataSetGallery(hostComposite, results);
			hostComposite.getParent().layout(true, true);
		} else {
			dataSetGallery.setInput(results);
		}
	}

	@Override
	public void saveState(IMemento memento) {
		if (getSourceViewer() != null) {
			super.saveState(memento);
		}
	}
	
	@Override
	public String getPartName() {
		return "Augmentations preview";
	}

}
