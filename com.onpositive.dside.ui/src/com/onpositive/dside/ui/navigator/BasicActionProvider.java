package com.onpositive.dside.ui.navigator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

public class BasicActionProvider extends CommonActionProvider {

	private OpenFileAction doubleClickAction;

	public BasicActionProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);

		doubleClickAction = new OpenFileAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());

		// only if doubleClickAction must know tree selection:
		
		aSite.getStructuredViewer().addSelectionChangedListener(doubleClickAction);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclips
	 * e.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		// forward doubleClick to doubleClickAction
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, doubleClickAction);
		IMenuManager menuManager = actionBars.getMenuManager();
		menuManager.add(new Action("Delete 2 ") {
		
		});
		menuManager.update(true);
		actionBars.updateActionBars();
	}
}
