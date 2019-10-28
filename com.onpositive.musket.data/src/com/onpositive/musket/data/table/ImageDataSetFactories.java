package com.onpositive.musket.data.table;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.onpositive.musket.data.columntypes.ClassColumnType;
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

public class ImageDataSetFactories implements IDataSetFactory{

	public ImageDataSetFactories() {
		super();
	}

	public IDataSet create(DataSetSpec spec, Map<String, Object> settings) {

		if (settings != null ) {
			Object object = settings.get(AbstractRLEImageDataSet.CLAZZ);
			if (object != null) {
				if (object.toString().equals(BinarySegmentationDataSet.class.getName())) {
					return new BinarySegmentationDataSet(spec.base(), settings, spec.getRepresenter());
				}
				if (object.toString().equals(MultiClassInstanceSegmentationDataSet.class.getName())) {
					return new MultiClassInstanceSegmentationDataSet(spec.base(), settings, spec.getRepresenter());
				}
				if (object.toString().equals(BinaryInstanceSegmentationDataSet.class.getName())) {
					return new BinaryInstanceSegmentationDataSet(spec.base(), settings, spec.getRepresenter());
				}
				if (object.toString().equals(MultiClassSegmentationDataSet.class.getName())) {
					return new MultiClassSegmentationDataSet(spec.base(), settings, spec.getRepresenter());
				}
				if (object.toString().equals(MultiClassificationDataset.class.getName())) {
					return new MultiClassificationDataset(spec.base(), settings, spec.getRepresenter());
				}
				if (object.toString().equals(BinaryClassificationDataSet.class.getName())) {
					return new BinaryClassificationDataSet(spec.base(), settings, spec.getRepresenter());
				}
			}
		}
		return innerCreate(spec);
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
		if (clazzColumn != null && rleColumn != null) {
			if (!multipleObjects || true) {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				MultiClassSegmentationDataSet multiClassSegmentationDataSet = new MultiClassSegmentationDataSet(spec,
						imageColumn, rleColumn, bufferedImage.getHeight(), bufferedImage.getWidth(), clazzColumn);
				Stream<IMultiClassSegmentationItem> filter = multiClassSegmentationDataSet.items().stream().parallel()
						.filter(x -> {
							return ((MultiClassSegmentationItem) x).hasSameClass();
						});
				Optional<IMultiClassSegmentationItem> findAny = filter.findAny();
				if (findAny.isPresent()) {
					multiClassSegmentationDataSet = new MultiClassInstanceSegmentationDataSet(spec.base(),
							multiClassSegmentationDataSet.getSettings(), images);
					return multiClassSegmentationDataSet;
				}
				return multiClassSegmentationDataSet;
			}
		}
		if (clazzColumn != null) {
			Collection<Object> values2 = new LinkedHashSet<>(clazzColumn.values());
			if (values2.size() == 2) {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				return new BinaryClassificationDataSet(spec.base(), imageColumn, clazzColumn, bufferedImage.getHeight(),
						bufferedImage.getWidth(), images);
			} else {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				MultiClassificationDataset multiClassificationDataset = new MultiClassificationDataset(spec.base(),
						imageColumn, clazzColumn, bufferedImage.getHeight(), bufferedImage.getWidth(), images);
				return multiClassificationDataset;
			}
		}
		return null;
	}

	@Override
	public String caption() {
		return "Image DataSet factories";
	}

	@Override
	public double estimate(DataSetSpec parameterObject) {
		IColumn imageColumn = parameterObject.getStrictColumn(ImageColumnType.class);
		if (imageColumn!=null) {
			return 1;
		}
		return 0;
	}
}