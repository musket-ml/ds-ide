package com.onpositive.musket.data.table;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.images.AbstractRLEImageDataSet;
import com.onpositive.musket.data.images.BinaryClassificationDataSet;
import com.onpositive.musket.data.images.BinaryInstanceSegmentationDataSet;
import com.onpositive.musket.data.images.BinarySegmentationDataSet;
import com.onpositive.musket.data.images.MultiClassSegmentationDataSet;
import com.onpositive.musket.data.images.MultiClassificationDataset;

public class ImageDataSetFactories {

	protected ImageRepresenter images;

	public ImageDataSetFactories(ImageRepresenter images) {
		super();
		this.images = images;
	}

	public IDataSet create(ITabularDataSet t, Map<String, Object> settings) {
		Object object = settings.get(AbstractRLEImageDataSet.CLAZZ);
		if (object != null) {
			// if (false) {
			if (object.toString().equals(BinarySegmentationDataSet.class.getName())) {
				return new BinarySegmentationDataSet(t, settings, images);
			}
			if (object.toString().equals(BinaryInstanceSegmentationDataSet.class.getName())) {
				return new BinaryInstanceSegmentationDataSet(t, settings, images);
			}
			if (object.toString().equals(MultiClassSegmentationDataSet.class.getName())) {
				return new MultiClassSegmentationDataSet(t, settings, images);
			}
			if (object.toString().equals(BinaryClassificationDataSet.class.getName())) {
				return new BinaryClassificationDataSet(t, settings, images);
			}
			// }
		}
		return create(t);
	}

	public IDataSet create(ITabularDataSet t) {
		IDataSet innerCreate = innerCreate(t);
		if (innerCreate == null) {
			List<? extends IColumn> columns = t.columns();
			ArrayList<IColumn> mm = new ArrayList<>();
			columns.forEach(v -> {
				if (v.id().contains("_")) {
					String[] split = v.id().split("_");
					int sn = 0;
					for (String s : split) {
						Column c = (Column) v;
						mm.add(new SubColumn(v.id(), s, c.num, c.clazz, sn));
						sn = sn + 1;
					}

				} else {
					mm.add(v.clone());
				}
			});
			BasicDataSetImpl newDs = new BasicDataSetImpl((ArrayList<? extends ITabularItem>) t.items(), mm);
			return innerCreate(newDs);
		}
		return innerCreate;
	}

	private IDataSet innerCreate(ITabularDataSet t) {
		List<? extends IColumn> columns = t.columns();

		IColumn imageColumn = null;
		
		
		for (IColumn c : columns) {
			if (images.like(c)) {
				if (imageColumn == null) {
					imageColumn = c;
				} else {
					return null;
				}
			}
		}
		if (imageColumn == null) {
			IColumn textColumn=null;
			for (IColumn c : columns) {
				if (isText(c)) {
					textColumn=c;
					break;
				}
			}
			if (textColumn!=null) {
				return TextDataSetFactories.create(t,textColumn);
			}
			return null;
		}
		ArrayList<IColumn> arrayList = new ArrayList<IColumn>(t.columns());
		arrayList.remove(imageColumn);
		Collection<Object> values = imageColumn.values();
		boolean multipleObjects = false;
		if (new HashSet<Object>(values).size() != values.size()) {
			multipleObjects = true;
		}
		IColumn maskColumn = null;

		
		for (IColumn c : arrayList) {
			if (c != imageColumn) {
				boolean like = new RLERepresenter().like(c);
				if (like) {
					if (maskColumn == null) {
						maskColumn = c;
					}
					if (like && arrayList.size() == 1) {
						if (!multipleObjects) {
							BufferedImage bufferedImage = images.get(images.iterator().next());
							return new BinarySegmentationDataSet(t, imageColumn, maskColumn, images,
									bufferedImage.getHeight(), bufferedImage.getWidth());
						} else {
							BufferedImage bufferedImage = images.get(images.iterator().next());
							return new BinaryInstanceSegmentationDataSet(t, imageColumn, maskColumn, images,
									bufferedImage.getHeight(), bufferedImage.getWidth());
						}
					}
				}
			}
		}
		if (maskColumn != null) {
			arrayList.remove(maskColumn);
		}
		IColumn clazzColumn = findClassColumn(arrayList);
		if (clazzColumn != null && maskColumn != null) {
			if (!multipleObjects || true) {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				return new MultiClassSegmentationDataSet(t, imageColumn, maskColumn, bufferedImage.getHeight(),
						bufferedImage.getWidth(), images, clazzColumn);
			}
		}
		if (clazzColumn != null) {
			Collection<Object> values2 = new LinkedHashSet<>(clazzColumn.values());
			if (values2.size() == 2) {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				return new BinaryClassificationDataSet(t, imageColumn, clazzColumn, bufferedImage.getHeight(),
						bufferedImage.getWidth(), images);
			} else {
				BufferedImage bufferedImage = images.get(images.iterator().next());
				return new MultiClassificationDataset(t, imageColumn, clazzColumn, bufferedImage.getHeight(),
						bufferedImage.getWidth(), images);
			}
		}
		return t;
	}

	protected static IColumn findClassColumn(ArrayList<IColumn> arrayList) {
		IColumn clazzColumn = null;
		for (IColumn m : arrayList) {
			if (m.caption().toLowerCase().contains("class") || m.caption().toLowerCase().contains("clazz")
					|| m.caption().toLowerCase().contains("classes")) {
				clazzColumn = m;
				break;
			}
			LinkedHashSet<Object> linkedHashSet = new LinkedHashSet<>(m.values());
			if (linkedHashSet.size() < 1000) {
				if (isAllInt(linkedHashSet) || isAllString(linkedHashSet) || isBool(linkedHashSet)) {
					clazzColumn = m;
					break;
				}
			}
		}
		return clazzColumn;
	}
	
	protected static ArrayList<IColumn> findClassColumns(ArrayList<IColumn> arrayList) {
		ArrayList<IColumn> clazzColumns = new ArrayList<>();
		for (IColumn m : arrayList) {
			if (m.caption().toLowerCase().contains("class") || m.caption().toLowerCase().contains("clazz")
					|| m.caption().toLowerCase().contains("classes")) {
				clazzColumns.add( m);
				continue;
			}
			LinkedHashSet<Object> linkedHashSet = new LinkedHashSet<>(m.values());
			if (linkedHashSet.size() < 1000) {
				
				if (isAllInt(linkedHashSet) || isAllString(linkedHashSet) || isBool(linkedHashSet)) {
					if(clazzColumns.isEmpty()) {
						clazzColumns.add( m);
					}
					else if (linkedHashSet.size()<100) {
						clazzColumns.add( m);
					}
					
				}
			}
		}
		return clazzColumns;
	}

	private static boolean isBool(LinkedHashSet<Object> linkedHashSet) {
		if (linkedHashSet.size() == 2) {
			return true;
		}
		return false;
	}

	private static boolean isAllString(LinkedHashSet<Object> linkedHashSet) {
		for (Object s0 : linkedHashSet) {
			String s = s0.toString();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				return false;
			}
			for (int i = 0; i < s.length(); i++) {
				if (!Character.isJavaIdentifierPart(s.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean isText(IColumn c) {
		return isText(c.values());
	}
	
	public static boolean isText(Collection<Object>linkedHashSet) {
		int textCount=0;
		for (Object o : linkedHashSet) {
			try {
				String string = o.toString();
				if (looksLikeText(string)) {
					textCount++;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return textCount>linkedHashSet.size()/2;
	}

	private static boolean looksLikeText(String string) {
		String[] split = string.split(" ");
		int words=0;
		if (split.length>2) {
			for (String m:split) {
				if (isWord(m)) {
					words++;
				}
			}
		}
		return words>3;
	}
	
	public static boolean isNumber(String m) {
		try {
			Double.parseDouble(m);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	private static boolean isWord(String m) {
		for (int i=0;i<m.length();i++) {
			char charAt = m.charAt(i);
			if (!Character.isLetter(charAt)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isAllInt(LinkedHashSet<Object> linkedHashSet) {
		for (Object o : linkedHashSet) {
			try {
				Integer.parseInt(o.toString());
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}
}
