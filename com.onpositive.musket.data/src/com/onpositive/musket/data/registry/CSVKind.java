package com.onpositive.musket.data.registry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

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
public class CSVKind implements IDataSetIO{

	@Override
	public IDataSet loadDataSet(DataSetMemento memento, IProgressMonitor monitor) {
		String url=memento.getUrl();
		String path=url.substring(url.indexOf("://")+3);
		try (CSVParser csvParser = CSVParser.parse(new File(path), Charset.forName("UTF-8"), CSVFormat.DEFAULT);) { 
//			csvParser.setMultilineLimit(100000);
				List<CSVRecord> records = csvParser.getRecords();
				ArrayList<Column>cs=new ArrayList<>();
				ArrayList<BasicItem>items=new ArrayList<BasicItem>();
				if (records.size()>0) {
					CSVRecord strings = records.get(0);
					int num=0;
					for (String s:strings) {
						Column orCreateColumn = getOrCreateColumn(s,records,memento,num++);
						
						cs.add(orCreateColumn);
					}
				}
				records=new ArrayList<>(records);
				for (int i=1;i<records.size();i++) {
					CSVRecord strings = records.get(i);
					Object[] dta=new Object[strings.size()];
					for (int j=0;j<strings.size();j++) {
						dta[j]=cs.get(j).parse(strings.get(j));
					}
					BasicItem basicItem = new BasicItem(i-1, dta);
					
					items.add(basicItem);
				}
				return new BasicDataSetImpl(items,cs);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		} catch (IOException e1) {
			throw new IllegalStateException(e1);
		}
	}

	private Column getOrCreateColumn(String s, List<CSVRecord> records, DataSetMemento memento,int num) {
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
		CSVPrinter csvWriter = new CSVPrinter(new FileWriter(path), CSVFormat.DEFAULT);
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
		csvWriter.printRecords(eee);
		csvWriter.close();
	}
}