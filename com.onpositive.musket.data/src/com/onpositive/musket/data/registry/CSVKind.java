package com.onpositive.musket.data.registry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.onpositive.musket.data.core.DataSetMemento;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetIO;
import com.onpositive.musket.data.core.IProgressMonitor;
import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.BasicItem;
import com.onpositive.musket.data.table.Column;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;
public class CSVKind implements IDataSetIO{

	@Override
	public IDataSet loadDataSet(DataSetMemento memento, IProgressMonitor monitor) {
		String url=memento.getUrl();
		String path=url.substring(url.indexOf("://")+3);
		try {
			CSVReader csvReader = new CSVReader(new FileReader(new File(path)),',','"' ,false);
			
			csvReader.setMultilineLimit(100000);
			try {
				List<String[]> readAll = csvReader.readAll();
				ArrayList<Column>cs=new ArrayList<>();
				ArrayList<BasicItem>items=new ArrayList<BasicItem>();
				if (readAll.size()>0) {
					String[] strings = readAll.get(0);
					int num=0;
					for (String s:strings) {
						Column orCreateColumn = getOrCreateColumn(s,readAll,memento,num++);
						
						cs.add(orCreateColumn);
					}
				}
				readAll=new ArrayList<>(readAll);
				for (int i=1;i<readAll.size();i++) {
					String[] strings = readAll.get(i);
					Object[] dta=new Object[strings.length];
					for (int j=0;j<strings.length;j++) {
						dta[j]=cs.get(j).parse(strings[j]);
					}
					BasicItem basicItem = new BasicItem(i-1, dta);
					
					items.add(basicItem);
				}
				return new BasicDataSetImpl(items,cs);
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					csvReader.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	private Column getOrCreateColumn(String s, List<String[]> readAll, DataSetMemento memento,int num) {
		return new Column(s, null, num, String.class);
	}

	@Override
	public void saveDataset(DataSetMemento memento, IDataSet set, IProgressMonitor monitor) {
		String url=memento.getUrl();
		String path=url.substring(url.indexOf("://")+3);
		try {
			writeCSV((ITabularDataSet) set, path);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void writeCSV(ITabularDataSet set, String path) throws IOException {
		CSVWriter csvWriter = new CSVWriter(new FileWriter(path));
		List<? extends IColumn> columns = set.columns();			
		ArrayList<String[]>eee=new ArrayList<String[]>();
		eee.add(columns.stream().map(x->x.id()).toArray(x->new String[x]));
		set.items().forEach(i->{
			String[]ll=new String[columns.size()];
			for (int j=0;j<columns.size();j++) {
				ll[j]=columns.get(j).getValueAsString((ITabularItem) i);
			}
			eee.add(ll);
			
		});
		csvWriter.writeAll(eee, false);
		csvWriter.close();
	}
}