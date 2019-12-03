package com.onpositive.musket.data.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IProgressMonitor;
import com.onpositive.musket.data.core.IPythonStringGenerator;
import com.onpositive.musket.data.images.AbstractImageDataSet;
import com.onpositive.musket.data.images.IBinaryClassificationDataSet;
import com.onpositive.musket.data.images.IBinarySegmentationDataSet;
import com.onpositive.musket.data.images.IImageDataSet;
import com.onpositive.musket.data.images.IImageItem;
import com.onpositive.musket.data.images.IMultiClassSegmentationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.MultiClassInstanceSegmentationDataSet;
import com.onpositive.musket.data.images.MultiClassSegmentationItem;
import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.registry.CSVKind;
import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.BasicItem;
import com.onpositive.musket.data.table.Column;
import com.onpositive.musket.data.table.ICSVOVerlay;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;
import com.onpositive.musket.data.text.TextClassificationDataSet;
import com.onpositive.semantic.model.api.property.java.annotations.Image;

public class BasicDataSetActions {

	public static BasicDataSetImpl toBinaryClassification(IBinaryClassificationDataSet ds) {
		ArrayList<Column> cs = new ArrayList<>();
		cs.add(new Column("ImageId", "ImageId", 0, String.class));
		cs.add(new Column("Class", "Class", 1, String.class));
		ArrayList<BasicItem> items = new ArrayList<>();
		ds.items().forEach(v -> {
			BasicItem item = new BasicItem(null,0, new Object[] { v.id(), v.isPositive() ? "1" : "0" });
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
			BasicItem item = new BasicItem(null,0,
					new Object[] { v.id(), v.originalclasses().stream().map(va->{
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
			BasicItem item = new BasicItem(null,0, new Object[] { v.id(), v.getMask().rle() });
			items.add(item);
		});
		return new BasicDataSetImpl(items, cs);
	}
	
	public static BasicDataSetImpl dropAttributes(IMultiClassSegmentationDataSet ds) {
		ArrayList<Column> cs = new ArrayList<>();
		cs.add(new Column("ImageId", "ImageId", 0, String.class));
		cs.add(new Column("EncodedPixels", "EncodedPixels", 1, String.class));
		cs.add(new Column("Height", "Height", 2, Integer.class));
		cs.add(new Column("Width", "Width", 3, Integer.class));
		cs.add(new Column("ClassId", "ClassId", 4, String.class));
		ArrayList<BasicItem> items = new ArrayList<>();
		ds.items().forEach(v -> {
			String imageId = v.id();
			MultiClassSegmentationItem vv = (MultiClassSegmentationItem)v;
			vv.getMasks().forEach(m->{
				int height = m.getHeight();
				int width = m.getWidth();
				String rle = m.rle();
				String classId = m.clazz();
				int ind = classId.indexOf("_");
				if(ind>=0) {
					classId = classId.substring(0, ind);
				}
				BasicItem item = new BasicItem(null,0, new Object[] {
						imageId, rle, height, width, classId});
				items.add(item);
			});
		});
		return new BasicDataSetImpl(items, cs);
	}
	
	
	public static BasicDataSetImpl saveWithCurrentFilters(IDataSet ds) {		
		ICSVOVerlay ov=(ICSVOVerlay) ds;
		ArrayList<ITabularItem>bi=new ArrayList<>();
		ds.items().forEach(v->{
			bi.addAll(ov.represents(v));
		});
		return (BasicDataSetImpl) ov.original().subDataSet("",bi);
	}
	
	public static BasicDataSetImpl recreateAsInstanceSegmentationDataset(IMultiClassSegmentationDataSet ds) {
		
		if(!(ds instanceof AbstractImageDataSet)) {
			throw new RuntimeException("Must implement '" + AbstractImageDataSet.class.getSimpleName() + "'");
		}
		AbstractImageDataSet<?> aids = (AbstractImageDataSet<?>)ds;
		ITabularDataSet tabular = aids.original();
		ImageRepresenter rep = aids.getRepresenter();		
		Map<String, Object> settings = ds.getSettings();
		MultiClassInstanceSegmentationDataSet mcisds = new MultiClassInstanceSegmentationDataSet(tabular, settings, rep);
		Object fPath = settings.get(DataProject.FILE_NAME);
		if(fPath==null) {
			throw new RuntimeException("Can not conver to Instance segmentation as file path is null");
		}
		File file2 = new File(fPath.toString());
		DataProject.dumpSettings(file2, mcisds, null);
		return null;
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
		
		protected boolean usesCurrentFilters;

		public boolean isUsesCurrentFilters() {
			return usesCurrentFilters;
		}
		public void setUsesCurrentFilters(boolean usesCurrentFilters) {
			this.usesCurrentFilters = usesCurrentFilters;
		}
		public void run(IDataSet v, File target) {
			@SuppressWarnings("unchecked")
			BasicDataSetImpl ds = ((Function<IDataSet, BasicDataSetImpl>) this.func).apply(v);
			try {
				if(ds != null) {
					CSVKind.writeCSV(ds, target.getAbsolutePath());
				}				
				
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
	
	public static class GenerateDataSetAction extends ConversionAction{
		public GenerateDataSetAction() {
			super("Generate Musket wrappers", null);
		}		
	}
	
	public static class ClearDataSetMeta extends ConversionAction{
		public ClearDataSetMeta() {
			super("Clear dataset metadata and reopen", null);
		}
	}
	
	public static class ConvertToInstanceSegmentation extends ConversionAction{
		public ConvertToInstanceSegmentation() {
			super("Convert to Multiclass Instance Sgmentation", (Function<IMultiClassSegmentationDataSet, BasicDataSetImpl>)BasicDataSetActions::recreateAsInstanceSegmentationDataset);
		}		
	}
	

	public static List<ConversionAction> getActions(IDataSet d) {
		
		ArrayList<ConversionAction> actions = new ArrayList<>();
		ConversionAction conversionAction = new ConversionAction("Save with current filters", BasicDataSetActions::saveWithCurrentFilters);
		conversionAction.setUsesCurrentFilters(true);
		actions.add(conversionAction);
		if (d instanceof IPythonStringGenerator) {
			actions.add(new GenerateDataSetAction());
		}
		if (d instanceof TextClassificationDataSet) {
			TextClassificationDataSet td=(TextClassificationDataSet) d;
			return actions;
		}
		
		if (d instanceof IBinaryClassificationDataSet) {
			Function<IBinaryClassificationDataSet, BasicDataSetImpl> v = BasicDataSetActions::toBinaryClassification;
			actions.add(new ConversionAction("Convert to Binary Classification", v));
		}
		if (d instanceof IMulticlassClassificationDataSet) {
			Function<IMulticlassClassificationDataSet, BasicDataSetImpl> v = BasicDataSetActions::toMultiClassClassification;
			actions.add(new ConversionAction("Convert to Multiclass Classification", v));
		}
		if (d instanceof IBinarySegmentationDataSet) {
			Function<IBinarySegmentationDataSet, BasicDataSetImpl> v = BasicDataSetActions::toBinarySegmentation;
			actions.add(new ConversionAction("Convert to Binary Segmentation", v));
		}
		if (d instanceof IMultiClassSegmentationDataSet && !(d instanceof MultiClassInstanceSegmentationDataSet)) {			
			actions.add(new ConvertToInstanceSegmentation());
		}
		if (d instanceof IImageDataSet) {
			actions.add(new ConvertResolutionAction());
		}
		if(d instanceof IMulticlassClassificationDataSet) {
			Function<IMultiClassSegmentationDataSet, BasicDataSetImpl> v = BasicDataSetActions::dropAttributes;
			actions.add(new ConversionAction("Drop Attributes", v));
		}
		return actions;
	}

}