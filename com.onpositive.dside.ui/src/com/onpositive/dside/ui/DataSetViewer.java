package com.onpositive.dside.ui;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.dside.ui.editors.DeprecatableEditorPart;
import com.onpositive.dside.ui.editors.ObjectEditorInput;
import com.onpositive.musket_core.DataSet;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class DataSetViewer extends DeprecatableEditorPart{

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
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void dispose() {
		images.values().forEach(i->i.dispose());
		//executor.shutdown();
		super.dispose();
		images=null;		
	}
	
	protected void loadImage(GalleryItem item) {
		Integer idx = (Integer)item.getData();
		if (images.containsKey(idx)) {
			item.setImage(images.get(idx));
		}
		tasks.offerLast(()->{	
			try {
			String s=dataset.item(idx).toString();
			Object id=dataset.id(idx);
			if (images!=null) {
				Image image = new Image(Display.getDefault(),s);
				images.put(idx, image);
				
			}
			
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					Integer data = (Integer)item.getData();
					if (images==null) {
						return;
					}
					if (images.containsKey(data)) {
						item.setImage(images.get(data));
						item.setText(""+id);
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
		if (gallery != null) {
			gallery.setFocus();
		}
	}
	
	@Override
	protected void setInput(IEditorInput input) {
		if (input instanceof ObjectEditorInput) {
			ObjectEditorInput objectEditorInput = (ObjectEditorInput) input;
			Object object = objectEditorInput.getObject();
			if (object instanceof DataSet) {
				this.dataset=(DataSet) object;
			}
		}				
	}

	@Override
	protected void createRegularContent(Composite parent) {
		Composite con = new Composite(parent, SWT.NONE);
		con.setLayout(new FillLayout());
		RootElement rl=new RootElement(con);
		IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(dataset,null,null);
		IUIElement<?> createWidget = widgetObject.createWidget(new Binding(dataset));
		rl.add((AbstractUIElement<?>) createWidget);
		con=(Composite) rl.getElement("inner").getControl();
		images = new LinkedHashMap<>();
		gallery = new Gallery(con, SWT.V_SCROLL | SWT.VIRTUAL);
		
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

}
