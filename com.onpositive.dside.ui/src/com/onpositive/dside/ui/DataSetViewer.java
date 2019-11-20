package com.onpositive.dside.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.musket_core.DataSet;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class DataSetViewer extends EditorPart{

	private DataSet dataset;
	private LinkedHashMap<Integer, Image> images;
	private Gallery gallery;
	
	private static LinkedBlockingDeque<Runnable> tasks=new LinkedBlockingDeque<>();

	static {
		Thread thread=new Thread() {
			public void run() {
				while (true) {
					try {
						Runnable pollLast = tasks.pollLast(1000, TimeUnit.MINUTES);
						try {
							pollLast.run();
						}catch (Exception e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		
	}

	@Override
	public void doSaveAs() {
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.setSite(site);
		super.setInput(input);
		ObjectEditorInput ed=(ObjectEditorInput) input;
		this.dataset=(DataSet) ed.getObject();
		
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		
		RootElement rl=new RootElement(parent);
		IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(dataset,null,null);
		IUIElement<?> createWidget = widgetObject.createWidget(new Binding(dataset));
		rl.add((AbstractUIElement<?>) createWidget);
		parent=(Composite) rl.getElement("inner").getControl();
		images = new LinkedHashMap<>();
		gallery = new Gallery(parent, SWT.V_SCROLL | SWT.VIRTUAL);
		
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setItemSize(356, 356);
		gr.setMinMargin(3);
		
		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		

		gallery.setGroupRenderer(gr);
		gallery.setItemRenderer(ir);
		
		//gallery.setVirtualGroups(true);
		int len=this.dataset.getLen();
		gallery.addListener(SWT.SetData, new Listener() {

			public void handleEvent(Event event) {
				GalleryItem item = (GalleryItem) event.item;
				
				int index;
				if (item.getParentItem() != null) {
					index = item.getParentItem().indexOf(item);
					item.setData(index);
					loadImage(item);
					item.setItemCount(0);
					item.setText(""+index);
				} else {
					index = gallery.indexOf(item);
					item.setItemCount(len);
					item.setExpanded(true);
					item.setText(dataset.getName()); //$NON-NLS-1$
					item.setData(index);
				}
			}

		});
		
		gallery.setItemCount(1);
	}
	@Override
	public void dispose() {
		images.values().forEach(i->i.dispose());
		//executor.shutdown();
		super.dispose();
		images=null;		
	}
	
	protected void loadImage(GalleryItem num) {
		Integer data = (Integer)num.getData();
		if (images.containsKey(num)) {
			num.setImage(images.get(data));
		}
		tasks.offerLast(()->{	
			try {
			String s=dataset.item(data).toString();
			Object id=dataset.id(data);
			if (images!=null) {
				Image image = new Image(Display.getDefault(),s);
				images.put(data, image);
				
			}
			
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					Integer data = (Integer)num.getData();
					if (images==null) {
						return;
					}
					if (images.containsKey(data)) {
						num.setImage(images.get(data));
						num.setText(""+id);
					}					
				}
			});
			}catch (Throwable e) {
				e.printStackTrace();
			}
		});		
	}

	@Override
	public void setFocus() {
		
	}
	
	

}
