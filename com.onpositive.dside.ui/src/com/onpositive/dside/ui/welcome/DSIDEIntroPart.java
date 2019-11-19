package com.onpositive.dside.ui.welcome;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.part.IntroPart;
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.onpositive.dside.ui.DSIDEUIPlugin;

public class DSIDEIntroPart extends IntroPart {

	private static final int MARGIN = 10;
	private static final RGB BACKGROUND = new RGB(116, 84, 125);
	private static final RGB FONT_FG = new RGB(246, 216, 255);
	private Browser browser;

	@Override
	public void standbyStateChanged(boolean standby) {
		// Ignore
	}

	@Override
	public void createPartControl(Composite parent) {
		Color bgColor = new Color(Display.getDefault(), BACKGROUND);
		Color fontFgColor = new Color(Display.getDefault(), FONT_FG);
		parent.setBackground(bgColor);

		PixelConverter converter = new PixelConverter(parent);
		GridLayoutFactory.fillDefaults().margins(converter.convertHorizontalDLUsToPixels(MARGIN),
				converter.convertHorizontalDLUsToPixels(MARGIN)).applyTo(parent);
		Composite topBarComposite = new Composite(parent, SWT.NONE);
		topBarComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(topBarComposite);
		topBarComposite.setBackground(bgColor);

		createTitledButton(topBarComposite, "New Musket project",
				DSIDEUIPlugin.loadImageDescriptor("images/welcome/lab48.png").createImage(),
				DSIDEUIPlugin.loadImageDescriptor("images/welcome/lab48_hov.png").createImage(), fontFgColor,
				Display.getDefault().getSystemColor(SWT.COLOR_WHITE), new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						openWizard("org.python.pydev.ui.wizards.project.MusketProjectWizard");
					}
				});

		createTitledButton(topBarComposite, "To the Workbench",
				DSIDEUIPlugin.loadImageDescriptor("images/welcome/workbench48.png").createImage(),
				DSIDEUIPlugin.loadImageDescriptor("images/welcome/workbench48_hov.png").createImage(), fontFgColor,
				Display.getDefault().getSystemColor(SWT.COLOR_WHITE), new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						closeIntro();
					}
				});
		
		Composite browserComp = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browserComp);
		browserComp.setBackground(bgColor);
		StackLayout stackLayout = new StackLayout();
		browserComp.setLayout(stackLayout);
		
		Composite loadingComp = new Composite(browserComp, SWT.NONE);
		loadingComp.setLayout(new GridLayout());
		Label loadingLbl = new Label(loadingComp,SWT.NONE);
		loadingLbl.setImage(DSIDEUIPlugin.getImageDescriptor("images/welcome/hourglass_256.png").createImage());
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(loadingLbl);
		stackLayout.topControl = loadingComp;
		
		browser = new Browser(browserComp, SWT.BORDER); 
		browser.setUrl("https://musket-ml.github.io/webdocs/");
		
		browser.addProgressListener(new ProgressListener() {
			
			@Override
			public void completed(ProgressEvent event) {
				Display.getDefault().asyncExec(() -> {
					stackLayout.topControl = browser;
					parent.layout(true, true);
				});
			}
			
			@Override
			public void changed(ProgressEvent event) {
				// Do nothing
			}
		});
	}

	protected TitledHyperlink createTitledButton(Composite topBarComposite, String title, Image image, Image hoverImage,
			Color fontFgColor, Color fontHoverColor, IHyperlinkListener hyperlinkListener) {
		TitledHyperlink newProjectLink = new TitledHyperlink(topBarComposite, SWT.NONE);
		newProjectLink.setForeground(fontFgColor);
		newProjectLink.setHoverForeground(fontHoverColor);
		newProjectLink.setImage(image);
		newProjectLink.setHoverImage(hoverImage);
		newProjectLink.setText(title);
		if (hyperlinkListener != null) {
			newProjectLink.addHyperlinkListener(hyperlinkListener);
		}
		return newProjectLink;
	}

	@Override
	public void setFocus() {
//		browser.setFocus();
	}

	public void openWizard(String id) {
		// First see if this is a "new wizard".
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
		// If not check if it is an "import wizard".
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
		}
		// Or maybe an export wizard
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
		}
		try {
			// Then if we have a wizard, open it.
			if (descriptor != null) {
				IWizard wizard = descriptor.createWizard();
				if (wizard instanceof IWorkbenchWizard) {
					IWorkbenchWizard workbenchWizard = (IWorkbenchWizard) wizard;
					workbenchWizard.init(PlatformUI.getWorkbench(), new StructuredSelection());
				}
				WizardDialog wd = new WizardDialog(getIntroSite().getShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());
				int res = wd.open();
				if (res == Window.OK) {
					closeIntro();
				}
			}
		} catch (CoreException e) {
			DSIDEUIPlugin.log(e);
		}
	}

	protected void closeIntro() {
		PlatformUI.getWorkbench().getIntroManager().closeIntro(DSIDEIntroPart.this);
	}

}
