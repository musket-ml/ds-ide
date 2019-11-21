package com.onpositive.musket.data.images;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.BasicItem;
import com.onpositive.musket.data.table.Column;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.table.ImageRepresenter;

public class FolderDataSet extends MultiClassificationDataset{

	public FolderDataSet(ITabularDataSet base2, IColumn image, IColumn clazzColumn, int width2, int height2,
			ImageRepresenter rep) {
		super(base2, image, clazzColumn, width2, height2, rep);
	}

	
	public static FolderDataSet createDataSetFromFolder(File folder) {
		HashMap<String, ArrayList<File>>images=new HashMap<>();
		fillMap(images,folder);
		List<Column> cs=new ArrayList<>();
		cs.add(new Column("ImageId", "ImageId",0, String.class));
		cs.add(new Column("Clazz", "Clazz",1, String.class));
		ArrayList<ITabularItem> items2 = new ArrayList<>();
		for (String s:images.keySet()) {
			ArrayList<File> arrayList = images.get(s);
			int num=0;
			for (File f:arrayList) {
				BasicItem it=new BasicItem(null,num, new String[] {f.getName(),s});
				num++;
				items2.add(it);
			}
		}
		BasicDataSetImpl impl=new BasicDataSetImpl(items2, cs);
		ImageRepresenter rep = new ImageRepresenter(folder.getAbsolutePath());
		rep.configure();
		if (rep.isEmpty()) {
			return null;
		}
		return new FolderDataSet(impl, cs.get(0), cs.get(1), 400, 400, rep);
	}
	protected String getPythonName() {
		if (this.classes.size()==1) {
			return "FolderDataSet";
		}
		return "FolderClassificationDataSet";
	}
	
	private static void fillMap(HashMap<String, ArrayList<File>> images, File folder) {
		for (File f:folder.listFiles()) {
			if (f.isDirectory()) {
				fillMap(images, f);
			}
			else {
				String name2 = f.getName();
				if (name2.endsWith(".jpg")||name2.endsWith(".png")||name2.endsWith(".gif")) {
					ArrayList<File> arrayList = images.get(folder.getName());
					if (arrayList==null) {
						arrayList=new ArrayList<>();
						images.put(folder.getName(), arrayList);
					}
					arrayList.add(f);
				}
			}
		}		
	}


	public static void main(String[] args) {
		FolderDataSet createDataSetFromFolder = createDataSetFromFolder(new File("D:\\tstw\\cats_vs_dogs\\data\\training_set\\training_set"));
		Image image = createDataSetFromFolder.items().iterator().next().getImage();
		System.out.println(image);
	}
}