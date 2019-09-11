package com.onpositive.musket.data.images.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IProgressMonitor;
import com.onpositive.musket.data.images.AbstractImageDataSet;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IBinarySegmentationDataSet;
import com.onpositive.musket.data.images.IImageDataSet;
import com.onpositive.musket.data.images.IImageItem;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.registry.CSVKind;
import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.BasicItem;
import com.onpositive.musket.data.table.Column;
import com.onpositive.semantic.model.api.property.java.annotations.Image;

public class BasicImageDataSetActions {

	public static BasicDataSetImpl toBinaryClassification(IBinaryClassificationDataSet ds) {
		ArrayList<Column> cs = new ArrayList<>();
		cs.add(new Column("ImageId", "ImageId", 0, String.class));
		cs.add(new Column("Class", "Class", 1, String.class));
		ArrayList<BasicItem> items = new ArrayList<>();
		ds.items().forEach(v -> {
			BasicItem item = new BasicItem(0, new Object[] { v.id(), v.isPositive() ? "1" : "0" });
			items.add(item);
		});
		return new BasicDataSetImpl(items, cs);
	}

	public static BasicDataSetImpl toMultiClassClassification(IMulticlassClassificationDataSet ds) {
		ArrayList<Column> cs = new ArrayList<>();
		cs.add(new Column("ImageId", "ImageId", 0, String.class));
		cs.add(new Column("Class", "Class", 1, String.class));
		ArrayList<BasicItem> items = new ArrayList<>();
		ds.items().forEach(v -> {
			BasicItem item = new BasicItem(0,
					new Object[] { v.id(), v.classes().stream().map(va->{
						if (va.equals("Empty")) {
							return "";
						}
						else {
							return va;
						}
					}).collect(Collectors.joining(" ")) });
			items.add(item);
		});
		return new BasicDataSetImpl(items, cs);
	}

	public static BasicDataSetImpl toBinarySegmentation(IBinarySegmentationDataSet ds) {
		ArrayList<Column> cs = new ArrayList<>();
		cs.add(new Column("ImageId", "ImageId", 0, String.class));
		cs.add(new Column("EncodedPixels", "EncodedPixels", 1, String.class));
		ArrayList<BasicItem> items = new ArrayList<>();
		ds.items().forEach(v -> {
			BasicItem item = new BasicItem(0, new Object[] { v.id(), v.getMask().rle() });
			items.add(item);
		});
		return new BasicDataSetImpl(items, cs);
	}
	
	

	@Image("")
	public static class ConversionAction {

		private Function<? extends IDataSet, BasicDataSetImpl> func;
		private String caption;

		public ConversionAction(String caption, Function<? extends IDataSet, BasicDataSetImpl> v) {
			this.func = v;
			this.caption = caption;
		}
		public String getCaption() {
			return caption;
		}

		public void run(IDataSet v, File target) {
			@SuppressWarnings("unchecked")
			BasicDataSetImpl ds = ((Function<IDataSet, BasicDataSetImpl>) this.func).apply(v);
			try {
				CSVKind.writeCSV(ds, target.getAbsolutePath());
			} catch (IOException e) {
				throw new IllegalStateException();
			}
		}
		@Override
		public String toString() {
			return this.caption;
		}
		@SuppressWarnings("unchecked")
		public void perform(IDataSet iDataSet, int width, int height,IProgressMonitor monitor) {
			AbstractImageDataSet<IImageItem>d=(AbstractImageDataSet<IImageItem>) iDataSet;
			d.getRepresenter().convertToResolution(monitor, width, height);
		}

	}
	public static class ConvertResolutionAction extends ConversionAction{

		public ConvertResolutionAction() {
			super("Convert images to another resolution", null);
		}
		
	}

	public static List<ConversionAction> getActions(IDataSet d) {
		ArrayList<ConversionAction> actions = new ArrayList<>();
		if (d instanceof IBinaryClassificationDataSet) {
			Function<IBinaryClassificationDataSet, BasicDataSetImpl> v = BasicImageDataSetActions::toBinaryClassification;
			actions.add(new ConversionAction("Convert to Binary Classification", v));
		}
		if (d instanceof IMulticlassClassificationDataSet) {
			Function<IMulticlassClassificationDataSet, BasicDataSetImpl> v = BasicImageDataSetActions::toMultiClassClassification;
			actions.add(new ConversionAction("Convert to Multiclass Classification", v));
		}
		if (d instanceof IBinarySegmentationDataSet) {
			Function<IBinarySegmentationDataSet, BasicDataSetImpl> v = BasicImageDataSetActions::toBinarySegmentation;
			actions.add(new ConversionAction("Convert to Binary Segmentation", v));
		}
		if (d instanceof IImageDataSet) {
			actions.add(new ConvertResolutionAction());
		}
		return actions;
	}

}