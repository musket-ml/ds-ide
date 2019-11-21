package com.onpositive.musket.data.table;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.onpositive.musket.data.columntypes.ClassColumnType;
import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.musket.data.columntypes.ImageColumnType;
import com.onpositive.musket.data.columntypes.RLEMaskColumnType;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.AbstractRLEImageDataSet;
import com.onpositive.musket.data.images.BinaryClassificationDataSet;
import com.onpositive.musket.data.images.BinaryInstanceSegmentationDataSet;
import com.onpositive.musket.data.images.BinarySegmentationDataSet;
import com.onpositive.musket.data.images.IMultiClassSegmentationItem;
import com.onpositive.musket.data.images.MultiClassInstanceSegmentationDataSet;
import com.onpositive.musket.data.images.MultiClassSegmentationDataSet;
import com.onpositive.musket.data.images.MultiClassSegmentationItem;
import com.onpositive.musket.data.images.MultiClassificationDataset;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.registry.CSVKind;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class ImageDataSetFactories implements IDataSetFactory {

	private static final String LABELS_PATH = "LABELS_PATH";

	public ImageDataSetFactories() {
		super();
	}

	public IDataSet create(DataSetSpec spec, Map<String, Object> settings) {

		if (settings != null) {
			Object object = settings.get(AbstractRLEImageDataSet.CLAZZ);
			IDataSet inner_Create = inner_Create(spec, settings, object);
			if (inner_Create != null) {
				Object object2 = settings.get(LABELS_PATH);
				if (object2 != null && inner_Create instanceof IHasLabels) {
					IDataSet load = DataSetIO.load("file://" + object2.toString());
					ITabularDataSet as = load.as(ITabularDataSet.class);
					if (as != null) {
						IHasLabels hl = (IHasLabels) inner_Create;
						LabelsSet labelsSet = new LabelsSet(as, (ArrayList<String>) new ArrayList<>(hl.classNames()));
						if (labelsSet.isOk()) {
							hl.setLabels(labelsSet);
						}
					}
				}
				return inner_Create;
			}

		}
		return innerCreate(spec);
	}

	protected IDataSet inner_Create(DataSetSpec spec, Map<String, Object> settings, Object object) {
		if (object != null) {
			if (object.toString().equals(BinarySegmentationDataSet.class.getName())) {
				return new BinarySegmentationDataSet(spec.tabularOrigin(), settings, spec.getRepresenter());
			}
			if (object.toString().equals(MultiClassInstanceSegmentationDataSet.class.getName())) {
				return new MultiClassInstanceSegmentationDataSet(spec.tabularOrigin(), settings, spec.getRepresenter());
			}
			if (object.toString().equals(BinaryInstanceSegmentationDataSet.class.getName())) {
				return new BinaryInstanceSegmentationDataSet(spec.tabularOrigin(), settings, spec.getRepresenter());
			}
			if (object.toString().equals(MultiClassSegmentationDataSet.class.getName())) {
				return new MultiClassSegmentationDataSet(spec.tabularOrigin(), settings, spec.getRepresenter());
			}
			if (object.toString().equals(MultiClassificationDataset.class.getName())) {
				return new MultiClassificationDataset(spec.tabularOrigin(), settings, spec.getRepresenter());
			}
			if (object.toString().equals(BinaryClassificationDataSet.class.getName())) {
				return new BinaryClassificationDataSet(spec.tabularOrigin(), settings, spec.getRepresenter());
			}
		}
		return null;
	}

	private IDataSet innerCreate(DataSetSpec spec) {
		List<? extends IColumn> columns = spec.columns();
		ImageRepresenter images = spec.getRepresenter();
		IColumn imageColumn = spec.getStrictColumn(ImageColumnType.class);
		if (imageColumn == null) {
			return null;
		}
		IColumn rleColumn = spec.getStrictColumn(RLEMaskColumnType.class);

		boolean multipleObjects = !imageColumn.unique();

		if (rleColumn != null) {
			// This is segmentation or instance segmentation we only need to understand what
			// kind of segmentation it is
			if (columns.size() == 2) {
				if (!multipleObjects) {
					BufferedImage bufferedImage = images.get(images.iterator().next());
					return new BinarySegmentationDataSet(spec, imageColumn, rleColumn, bufferedImage.getHeight(),
							bufferedImage.getWidth());
				} else {
					BufferedImage bufferedImage = images.get(images.iterator().next());
					return new BinaryInstanceSegmentationDataSet(spec, imageColumn, rleColumn,
							bufferedImage.getHeight(), bufferedImage.getWidth());
				}
			}
		}

		IColumn clazzColumn = spec.getStrictColumn(ClassColumnType.class);
		Collection<ColumnInfo> infos = spec.layout.infos();
		
		LinkedHashSet<IColumn>allClasses=new LinkedHashSet<>();
		for (ColumnInfo i:infos) {
			if(i.preferredType()==ClassColumnType.class) {
				allClasses.add(i.getColumn());
				
			}
		}		
		if (allClasses.size()>1) {
			boolean askQuestion = spec.answerer.askQuestion("We have detected multiple classification columns, please select columns that you would like to use?", 
					allClasses);
			if (!askQuestion||allClasses.isEmpty()) {
				return null;
			}
			//we have multiple class columns
		}
		
		if (clazzColumn != null && rleColumn != null) {
			if (!multipleObjects || true) {
				if (allClasses.size()>1) {
					spec.answerer.askQuestion("Sorry, segmentation and instance segmentation datasets, does not support multiple class columns yet, switching to generic",false);
					return null;
				}
				clazzColumn=allClasses.iterator().next();
				ArrayList<Object> uniqueValues = clazzColumn.uniqueValues();
				if (uniqueValues.contains("")) {
					spec.answerer.askQuestion("Sorry, class column can not be empty for segmentation and instance segmentation datasets,  switching to generic",false);
					return null;
				}
				if (uniqueValues.size()>1000) {
					for (Object s:uniqueValues) {
						if (s!=null) {
							String m=s.toString();
							if (m.contains(" ")||m.contains("|")||m.contains("_")) {
								spec.answerer.askQuestion("Sorry, class column can not have multiple classes at once for segmentation and instance segmentation datasets,  switching to generic",false);
								
								return null;			
							}
						}
						
					}
				}
				BufferedImage bufferedImage = images.get(images.iterator().next());
				MultiClassSegmentationDataSet multiClassSegmentationDataSet = new MultiClassSegmentationDataSet(spec,
						imageColumn, rleColumn, bufferedImage.getHeight(), bufferedImage.getWidth(), clazzColumn);
				Stream<IMultiClassSegmentationItem> filter = multiClassSegmentationDataSet.items().stream().parallel()
						.filter(x -> {
							return ((MultiClassSegmentationItem) x).hasSameClass();
						});
				Optional<IMultiClassSegmentationItem> findAny = filter.findAny();
				trySetupLabels(spec, multiClassSegmentationDataSet);
				if (findAny.isPresent()) {
					multiClassSegmentationDataSet = new MultiClassInstanceSegmentationDataSet(spec.tabularOrigin(),
							multiClassSegmentationDataSet.getSettings(), images);
					multiClassSegmentationDataSet.setLabels(multiClassSegmentationDataSet.labels());
					return multiClassSegmentationDataSet;
				}
				return multiClassSegmentationDataSet;
			}
		}
		
		if (clazzColumn != null) {
			Collection<Object> values2 = new LinkedHashSet<>(clazzColumn.values());
			if (values2.size() == 2) {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				return new BinaryClassificationDataSet(spec.tabularOrigin(), imageColumn, clazzColumn, bufferedImage.getHeight(),
						bufferedImage.getWidth(), images);
			} else {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				MultiClassificationDataset multiClassificationDataset = new MultiClassificationDataset(spec.tabularOrigin(),
						imageColumn, clazzColumn, bufferedImage.getHeight(), bufferedImage.getWidth(), images);
				trySetupLabels(spec, multiClassificationDataset);
				return multiClassificationDataset;
			}
		}
		return null;
	}

	protected void trySetupLabels(DataSetSpec spec, IHasLabels multiClassificationDataset) {
		List<String> classNames = multiClassificationDataset.classNames();
		if (classNames.size() > 2) {
			if (!BinaryClassificationDataSet.isStringClasses(multiClassificationDataset.classNames())) {
				File file = spec.prj.getFile();
				for (File f : file.listFiles()) {
					if (f.getName().contains("label") && f.getName().endsWith(".csv")) {
						try {
							IDataSet load = DataSetIO.load("file://" + f.getAbsolutePath());
							ITabularDataSet as = load.as(ITabularDataSet.class);
							if (as != null) {
								LabelsSet labelsSet = new LabelsSet(as,
										(ArrayList<String>) new ArrayList<>(multiClassificationDataset.classNames()));
								if (labelsSet.isOk()) {
									multiClassificationDataset.setLabels(labelsSet);
									multiClassificationDataset.getSettings().put(LABELS_PATH, f.getAbsolutePath());
									return;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				boolean ddd = spec.answerer.askQuestion(
						"This dataset has " + classNames.size() + " classes, do you want to configure labels for them?",
						Boolean.TRUE);
				if (ddd) {
					LabelsSet ls = new LabelsSet(new ArrayList<>(multiClassificationDataset.classNames()));
					boolean askQuestion = spec.answerer.askQuestion("", ls);
					if (askQuestion) {
						ITabularDataSet data = ls.getData();
						String absolutePath = new File(spec.prj.getFile(),"labels.csv").getAbsolutePath();
						try {
							CSVKind.writeCSV(data, absolutePath);
							multiClassificationDataset.setLabels(ls);
							multiClassificationDataset.getSettings().put(LABELS_PATH, absolutePath);
							return;							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
	}

	@Override
	public String caption() {
		return "Image DataSet factories";
	}

	@Override
	public double estimate(DataSetSpec parameterObject) {
		IColumn imageColumn = parameterObject.getStrictColumn(ImageColumnType.class);
		if (imageColumn != null) {
			return 1;
		}
		return 0;
	}
}