package com.onpositive.musket.data.registry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
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
		try {
			return doReadApacheCSV(path,memento.getEncoding());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected IDataSet doReadApacheCSV(String path,String encoding) {
		CSVFormat default1 = CSVFormat.DEFAULT;
		InputStreamReader fileReader=null;
		FileInputStream fileInputStream=null;
		try {
			fileInputStream = new FileInputStream(path);
			fileReader = new InputStreamReader(fileInputStream,encoding);
		} catch (FileNotFoundException e1) {
			return null;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		ArrayList<Column>cs=new ArrayList<>();
		ArrayList<BasicItem>items=new ArrayList<BasicItem>();
		try {
		Reader in = new BufferedReader(fileReader);
		int num=0;
		
		for (CSVRecord record : default1.parse(in)) {
			if (num==0) {
				int n=0;
				for (String s:record) {
					Column orCreateColumn = getOrCreateColumn(s,null,null,n++);
					cs.add(orCreateColumn);
				}
				num++;
				continue;
			}
			if (record.isConsistent()) {
				String[] vals=new String[cs.size()];
				for (int i=0;i<vals.length;i++) {
					vals[i]=record.get(i);
				}
				BasicItem basicItem = new BasicItem(num-1, vals);
				items.add(basicItem);
				num++;
			}
			else {
				in.close();
				return null;
			}			
		 }
		 in.close();
		 fileInputStream.close();
		} catch (IOException e1) {
			return null;
		}finally {
			try {
				fileReader.close();
				
			} catch (IOException e1) {
				throw new IllegalStateException(e1);
			}
		}
		return new BasicDataSetImpl(items,cs);
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