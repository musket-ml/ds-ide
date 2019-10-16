package com.onpositive.musket.data.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.onpositive.musket.data.columntypes.ColumnLayout;
import com.onpositive.musket.data.columntypes.DataSetFactoryRegistry;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.images.NotEnoughParametersException;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ImageDataSetFactories;
import com.onpositive.musket.data.table.ImageRepresenter;

public class DataProject {

	private static final String DATASET_FACTORY = "dataset_factory";
	private ImageRepresenter imageRepresenter;

	public DataProject(File file) {
		imageRepresenter = new ImageRepresenter(file.getAbsolutePath());
		imageRepresenter.configure();
	}

	public ImageRepresenter getRepresenter() {
		return imageRepresenter;
	}

	@SuppressWarnings("unchecked")
	public IDataSet getDataSet(File file2, IQuestionAnswerer answerer) {
		try {
			long l0=System.currentTimeMillis();
			IDataSet load = DataSetIO.load("file://" + file2.getAbsolutePath());
			if (load == null) {
				return null;
			}
			ITabularDataSet t1 = load.as(ITabularDataSet.class);
			if (getMetaFile(file2).exists()) {
				try {
					FileReader fileReader = new FileReader(getMetaFile(file2));
					@SuppressWarnings("rawtypes")
					Map loadAs = new Yaml().loadAs(fileReader, Map.class);
					fileReader.close();
					Object object = loadAs.get(DATASET_FACTORY);
					if (object != null) {
						try {
							IDataSetFactory factory = (IDataSetFactory) Class.forName(object.toString()).newInstance();
							IDataSet create = factory.create(new DataSetSpec(null, t1, this, answerer), loadAs);
							return create;
						} catch (Exception e) {
							e.printStackTrace();
							// TODO: handle exception
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
			}
			long l1=System.currentTimeMillis();
			System.out.println("CSV Load:"+(l1-l0));
			List<IColumn> columns = (List<IColumn>) t1.columns();
			ColumnLayout layout = new ColumnLayout(columns, this, answerer);
			DataSetSpec spec = new DataSetSpec(layout, layout.getNewDataSet(), this, answerer);
			// make it a little bit smarter
			IDataSetFactory factory = null;
			long l2=System.currentTimeMillis();
			System.out.println("Column parsing:"+(l2-l1));
			ArrayList<IDataSetFactory> matching = DataSetFactoryRegistry.getInstance().matching(spec);
			IDataSet create = null;
			if (!matching.isEmpty() && matching.size() > 1) {
				FactoryModel model = new FactoryModel(matching);
				boolean askQuestion = answerer.askQuestion("Please select factory", model);
				if (askQuestion) {
					create = model.selected.create(spec, null);
					factory = model.selected;
				}
			} else if (matching.size() == 1) {
				IDataSetFactory iDataSetFactory = matching.get(0);
				factory = iDataSetFactory;
				create = iDataSetFactory.create(spec, null);
			}
			long l3=System.currentTimeMillis();
			System.out.println("Dataset creation:"+(l3-l2));
			if (create != null) {
				dumpSettings(file2, create, factory);
				return create;
			}
			
			return new GenericDataSet(spec, t1);

		} catch (NotEnoughParametersException e) {
			return null;
		}
	}

	protected void dumpSettings(File file2, IDataSet create, IDataSetFactory factory) {
		if (create != null) {
			try {
				FileWriter fileWriter = new FileWriter(getMetaFile(file2));
				Map<String, Object> settings = create.getSettings();
				settings.put(DATASET_FACTORY, factory.getClass().getName());
				new Yaml().dump(settings, fileWriter);
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static File getMetaFile(File file2) {
		return new File(file2.getParentFile(), "." + file2.getName() + ".dataset_desc");
	}

}