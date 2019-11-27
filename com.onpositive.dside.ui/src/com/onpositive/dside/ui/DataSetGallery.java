package com.onpositive.dside.ui;

import java.util.LinkedHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.onpositive.musket_core.IDataSet;
import com.onpositive.semantic.ui.core.Alignment;
import com.onpositive.semantic.ui.core.Point;

public class DataSetGallery extends VisualizerViewer<Control> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int SIZE_STEP = 16;
	private static final int ITEM_HEIGHT = 56;
	
	private LinkedHashMap<It, Image> images;
	private Gallery gallery;

	private static ImageDescriptor zoom = AbstractUIPlugin.imageDescriptorFromPlugin((String) "com.onpositive.dside.ui",
			(String) "/icons/zoom_in.gif");
	private static ImageDescriptor zoomout = AbstractUIPlugin.imageDescriptorFromPlugin((String) "com.onpositive.dside.ui",
			(String) "/icons/zoom_out.gif");
	private static ImageDescriptor clearCo = AbstractUIPlugin.imageDescriptorFromPlugin((String) "com.onpositive.dside.ui",
			(String) "/icons/clear_co.gif");
	private static ImageDescriptor collapse = AbstractUIPlugin.imageDescriptorFromPlugin((String) "com.onpositive.dside.ui",
			(String) "/icons/collapseall.gif");
	private static ImageDescriptor expand = AbstractUIPlugin.imageDescriptorFromPlugin((String) "com.onpositive.dside.ui",
			(String) "/icons/expandall.gif");
	private com.onpositive.dside.ui.GalleryTooltip tooltip;

	@Override
	protected Control createControl(Composite conComposite) {
		images = new LinkedHashMap<>();
		gallery = new Gallery(conComposite, SWT.V_SCROLL | SWT.VIRTUAL);
		this.tooltip = new GalleryTooltip((Control)gallery, gallery);
		conComposite.setLayout(new FillLayout());
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setItemSize(356, 356);
		gr.setMinMargin(3);

		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();

		gallery.setGroupRenderer(gr);
		gallery.setItemRenderer(ir);
		getLayoutHints().setGridy(true);
		getLayoutHints().setAlignmentHorizontal(Alignment.FILL);
		getLayoutHints().setAlignmentVertical(Alignment.FILL);
		getLayoutHints().setSpan(new Point(3, 1));
		// gallery.setVirtualGroups(true);
		// int len=this.input.getLen();
		gallery.addListener(SWT.SetData, new Listener() {

			public void handleEvent(Event event) {
				GalleryItem item = (GalleryItem) event.item;

				int index;
				if (item.getParentItem() != null) {
					GalleryItem parentItem = item.getParentItem();
					index = parentItem.indexOf(item);
					item.setData(index);
					loadImage(item);
					item.setItemCount(0);
					item.setText("" + index);
				} else {
					index = gallery.indexOf(item);
					IDataSet data = input.get(index);
					int len = data.len();
					item.setItemCount(len);
					item.setExpanded(true);
					item.setText(data.get_name()); // $NON-NLS-1$
					item.setData(data);
				}

				// Your image here
				// item.setImage(eclipseImage);
			}

		});

		gallery.setItemCount(input.size());
		fillContextMenu(gallery, gr);
		return gallery;
	}

	private class CopyAction extends Action {

		public CopyAction() {
			super("Copy", SWT.PUSH);
		}

		public void run() {
			GalleryItem[] selection = gallery.getSelection();

			if (selection == null || selection.length == 0) {
				return;
			}
			boolean imageTransfer = ImageTransferWrapper.isAvalable();
			StringBuilder builderData = new StringBuilder();
			String[] fileData = new String[selection.length];
			
			ImageData imageData = null;
			if (imageTransfer) {
				try {
					imageData = selection[0].getImage().getImageData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Clipboard clipboard = new Clipboard(Display.getCurrent());
			Object[] contents = new Object[] {  imageData };					
			Transfer[] transfers = imageTransfer
					? new Transfer[] { 
							(Transfer) ImageTransferWrapper.getInstance() }
					: new Transfer[] { FileTransfer.getInstance(), TextTransfer.getInstance() };
			clipboard.setContents(contents, transfers);
			clipboard.dispose();
		}

	}

	private void zoomOut(DefaultGalleryGroupRenderer gr) {
		int itemHeight = gr.getItemHeight();
		int itemWidth = gr.getItemWidth();
		if (itemHeight > 40 && itemWidth > ITEM_HEIGHT) {
			itemHeight -= SIZE_STEP;
			itemWidth -= SIZE_STEP;
		}
		gr.setItemSize(itemWidth, itemHeight);
	}

	private void zoomIn(DefaultGalleryGroupRenderer gr) {
		int itemHeight = gr.getItemHeight();
		int itemWidth = gr.getItemWidth();
		if (itemHeight < 1024) {
			itemHeight += SIZE_STEP;
			itemWidth += SIZE_STEP;
		}
		gr.setItemSize(itemWidth, itemHeight);
	}

	private void fillContextMenu(final Gallery gallery, final DefaultGalleryGroupRenderer gr) {
		MenuManager manager = (MenuManager) this.getActualPopupMenuManager();
		manager.setRemoveAllWhenShown(false);
		manager.add(new CopyAction());
		manager.add((IContributionItem) new Separator());
		Action action = new Action("Zoom In", 1) {

			public void run() {
				zoomIn(gr);
			}
		};
		action.setImageDescriptor(this.zoom);
		manager.add((IAction) action);
		Action action2 = new Action("Zoom Out", 1) {

			public void run() {
				zoomOut(gr);
			}
		};
		action2.setImageDescriptor(this.zoomout);
		manager.add((IAction) action2);
		Menu createContextMenu = manager.createContextMenu((Control) gallery);
		gallery.setMenu(createContextMenu);
	}

	@Override
	public void dispose() {
		images.values().forEach(i -> {
			i.dispose();
		});
		super.dispose();
		images = null;
	}

	

	protected void loadImage(GalleryItem galleryItem) {
		Integer data = (Integer) galleryItem.getData();
		IDataSet ds = (IDataSet) galleryItem.getParentItem().getData();
		It key = new It(data, ds);
		if (images.containsKey(key)) {
			galleryItem.setImage(images.get(key));
		}
		tasks.offerLast(() -> {
			try {
				Object item = null;
				Object id=null;
				try {
					item = ds.item(data);
					id=ds.id(data);
				} catch (Exception e) {
				}
				final String lid=""+id;
				String filename = item != null ? item.toString() : null;
				if (images != null) {

					Image image = null;
					if (filename != null) {
						image = new Image(Display.getDefault(), filename);
					} else {
						image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
						Image newImg = new Image(Display.getDefault(), 256, 256);
						GC gc = new GC(newImg);
						gc.drawImage(image, 80, 122);
						gc.drawText("Error during visualization", 100, 120);
						gc.dispose();
						image = newImg;
					}
					images.put(key, image);
				}

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						Integer data = (Integer) galleryItem.getData();
						IDataSet ds = (IDataSet) galleryItem.getParentItem().getData();
						It key = new It(data, ds);
						if (images == null) {
							return;
						}
						if (images.containsKey(key)) {
							galleryItem.setImage(images.get(key));
							galleryItem.setText(lid);
						}
					}
				});
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public boolean needsLabel() {
		return false;
	}
	
	@Override
	public Control getControl() {
		Control control = super.getControl();
		if (control != null) {
			return control;
		}
		return gallery;
	}

}
