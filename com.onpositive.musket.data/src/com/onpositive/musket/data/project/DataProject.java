package com.onpositive.musket.data.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.onpositive.musket.data.columntypes.ColumnLayout;
import com.onpositive.musket.data.columntypes.DataSetFactoryRegistry;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IProgressMonitor;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.generic.GenericDataSetFactory;
import com.onpositive.musket.data.images.NotEnoughParametersException;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ImageRepresenter;
import com.onpositive.yamledit.io.YamlIO;

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
	public IDataSet getDataSet(File file2, IQuestionAnswerer answerer, IProgressMonitor monitor) {
		try {
			long l0=System.currentTimeMillis();
			monitor.onProgress("Loading dataset into memory", 0);
			IDataSet load = DataSetIO.load("file://" + file2.getAbsolutePath());
			if (load == null) {
				return null;
			}
			ITabularDataSet t1 = load.as(ITabularDataSet.class);
			
			long l1=System.currentTimeMillis();
			System.out.println("CSV Load:"+(l1-l0));
			if (getMetaFile(file2).exists()) {
				try {
					FileReader fileReader = new FileReader(getMetaFile(file2));
					@SuppressWarnings("rawtypes")
					Map loadAs = YamlIO.loadAs(fileReader, Map.class);
					fileReader.close();
					Object object = loadAs.get(DATASET_FACTORY);
					if (object != null) {
						try {
							IDataSetFactory factory = (IDataSetFactory) Class.forName(object.toString()).newInstance();
							boolean onProgress = monitor.onProgress("Preparing dataset for use", 1);
							if (!onProgress) {
								return null;
							}
							IDataSet create = factory.create(new DataSetSpec(null, t1, this, answerer), loadAs);
							monitor.onDone("All done", 2);
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
			
			boolean onProgress = monitor.onProgress("Analizing dataset layout", 1);
			if (!onProgress) {
				return null;
			}
			List<IColumn> columns = (List<IColumn>) t1.columns();
			ColumnLayout layout = new ColumnLayout(columns, this, answerer,monitor);
			DataSetSpec spec = new DataSetSpec(layout, layout.getNewDataSet(), this, answerer);
			// make it a little bit smarter
			IDataSetFactory factory = null;
			long l2=System.currentTimeMillis();
			System.out.println("Column parsing:"+(l2-l1));
			boolean onProgress2 = monitor.onProgress("Checking data", 1);
			if (!onProgress2) {
				return null;
			}
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
			GenericDataSet genericDataSet = new GenericDataSet(spec, t1);
			dumpSettings(file2, genericDataSet, new GenericDataSetFactory());
			return genericDataSet;

		} catch (NotEnoughParametersException e) {
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public IDataSet openGeneric(ITabularDataSet t1, IQuestionAnswerer answerer, IProgressMonitor monitor) {
		try {
			
			boolean onProgress = monitor.onProgress("Analizing dataset layout", 1);
			if (!onProgress) {
				return null;
			}
			List<IColumn> columns = (List<IColumn>) t1.columns();
			ColumnLayout layout = new ColumnLayout(columns, this, answerer,monitor);
			DataSetSpec spec = new DataSetSpec(layout, layout.getNewDataSet(), this, answerer);
			long l2=System.currentTimeMillis();
			GenericDataSet genericDataSet = new GenericDataSet(spec, t1);
			return genericDataSet;

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
				YamlIO.dump(settings, fileWriter);
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