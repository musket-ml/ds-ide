package com.onpositive.dside.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.python.pydev.shared_core.log.Log;

/**
 * Forked from PyDev UI utils 
 *
 */
public class WorkbenchUIUtils {
   public static Display getDisplay() {
      return PlatformUI.getWorkbench().getDisplay();
   }

   public static Shell getActiveShell() {
      Shell shell = getDisplay().getActiveShell();
      if (shell == null) {
         IWorkbenchWindow window = getActiveWorkbenchWindow();
         if (window != null) {
            shell = window.getShell();
         }      }

      return shell;
   }

   public static IWorkbenchWindow getActiveWorkbenchWindow() {
      if (!PlatformUI.isWorkbenchRunning()) {
         return null;
      } else {
         IWorkbench workbench = PlatformUI.getWorkbench();
         return workbench == null ? null : workbench.getActiveWorkbenchWindow();
      }
   }



   public static IEditorPart getActiveEditor() {
      IWorkbenchPage workbenchPage = getActivePage();
      return workbenchPage == null ? null : workbenchPage.getActiveEditor();
   }




   public static IWorkbenchPart getActivePart() {
      IWorkbenchPage workbenchPage = getActivePage();
      return workbenchPage == null ? null : workbenchPage.getActivePart();
   }




   public static IWorkbenchPage getActivePage() {
      IWorkbenchWindow workbench = getActiveWorkbenchWindow();
      return workbench == null ? null : workbench.getActivePage();
   }





   public static Display getStandardDisplay() {
      Display display = Display.getCurrent();
      if (display == null) {
         display = Display.getDefault();
      }
      return display;
   }

   public static ViewPart getView(String viewId, boolean forceVisible) {
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      try {
         if (workbenchWindow == null) {
            return null;
         }
         IWorkbenchPage page = workbenchWindow.getActivePage();
         if (forceVisible) {
            return (ViewPart)page.showView(viewId, (String)null, 2);
         }

         IViewReference viewReference = page.findViewReference(viewId);
         if (viewReference != null) {


            return (ViewPart)viewReference.getView(false);

         }
      } catch (Exception var5) {
         Log.log(var5);
      }
      return null;
   }
}
