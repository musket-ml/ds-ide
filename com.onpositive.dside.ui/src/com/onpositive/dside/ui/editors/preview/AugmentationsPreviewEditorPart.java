package com.onpositive.dside.ui.editors.preview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.StandaloneDataSetGallery;

public class AugmentationsPreviewEditorPart extends MusketPreviewEditorPart {
	
	public static final String ID = "com.onpositive.dside.ui.editors.augmentation.preview";

	private StandaloneDataSetGallery dataSetGallery;
	private Composite hostComposite;

	@Override
	protected void createPreviewControl(Composite parent) {
		hostComposite = new Composite(parent, SWT.NONE);
		hostComposite.setLayout(new FillLayout());
		Label waitLbl = new Label(hostComposite, SWT.NONE);
		waitLbl.setText("Preview is unavailable yet");
	}

	@Override
	protected void doRefreshPreview(IAnalizeResults results) {
		for (Control child : hostComposite.getChildren()) {
			child.dispose();
		}
		dataSetGallery = new StandaloneDataSetGallery(hostComposite, results);
		hostComposite.getParent().layout(true, true);
	}

}
