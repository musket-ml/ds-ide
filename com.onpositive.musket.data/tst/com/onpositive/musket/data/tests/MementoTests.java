package com.onpositive.musket.data.tests;


import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

import org.junit.Test;

import com.onpositive.musket.data.core.DataSetMemento;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.registry.DataSetIO;
import com.onpositive.musket.data.table.BasicDataSetImpl;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class MementoTests {

	@Test
	public void test() {
		DataSetMemento dataSetMemento = new DataSetMemento("file://D:/sw2/data/train-rle.csv");
		IDataSet loadDataSet = DataSetIO.getInstance().loadDataSet(dataSetMemento, null);
		int size = loadDataSet.items().size();
		System.out.println(size);
	}
	
	@Test
	public void test22() {
		DataSetMemento dataSetMemento = new DataSetMemento("file://D:\\eclipse-workspace\\com.onpositive.musket.data\\tst\\com\\onpositive\\musket\\data\\tests\\f1.csv");
		ITabularDataSet loadDataSet = DataSetIO.getInstance().loadDataSet(dataSetMemento, null).as(ITabularDataSet.class);
		
		DataSetMemento dataSetMemento2 = new DataSetMemento("file://D:\\eclipse-workspace\\com.onpositive.musket.data\\tst\\com\\onpositive\\musket\\data\\tests\\f1Stage2-retestStage1FromScratch.csv");
		BasicDataSetImpl loadDataSet2 = DataSetIO.getInstance().loadDataSet(dataSetMemento2, null).as(BasicDataSetImpl.class);
		int size = loadDataSet.items().size();
		System.out.println(size);
		
		int a=0;
		double maxDiff=0;
		for (ITabularItem i:loadDataSet.items()) {
			
			ITabularItem iTabularItem = loadDataSet2.get(a);
			
			double value = i.doubleValue("seg");
			double value1 = iTabularItem.doubleValue("seg");
			double abs = Math.abs(value-value1);
			if (abs>0.02&&value>0.1&&value<0.9) {
				System.out.println(value+","+value1+","+i.value("ImageId"));
			}
			a=a+1;
		}
		System.out.println(maxDiff);
	}

	int count;
	@Test
	public void test1() {
		
		ITabularDataSet t2 = DataSetIO.load("file://D:\\eclipse-workspace\\com.onpositive.musket.data\\tst\\com\\onpositive\\musket\\data\\tests\\subm1118-2.csv").as(ITabularDataSet.class);
		
		ITabularDataSet t3 = DataSetIO.load("file://D:\\eclipse-workspace\\com.onpositive.musket.data\\tst\\com\\onpositive\\musket\\data\\tests\\subm1118-4-test.csv").as(ITabularDataSet.class);
		//ITabularDataSet t3 = DataSetIO.load("file://D:/s/subm_se5_n45.csv").as(ITabularDataSet.class);
		
		
		IDataSetDelta compare = t2.compare(t3);
		
		IColumn column = t2.columns().get(1);
		Function<ITabularItem, Object> func = x->{
			return !column.getValue(x).equals("-1");			
		};
		
		//t1.addColumn("positive", func);
		t2.addColumn("positive", func);
		t3.addColumn("positive", func);
		
		//IColumn id = t1.columns().get(0);
		HashMap<Integer, HashSet<Object>>vs=new HashMap<Integer, HashSet<Object>>();
		int c=0;
		for (int i=0;i<t2.length();i++) {
			Object value = t2.get(i).value("positive");
			if (t2.length()<=i)
				break;
			Object value1 = t2.get(i).value("positive");
			Object value2 = t3.get(i).value("positive");
			HashSet<Object> hashSet = new HashSet<Object>();
			hashSet.add(value);
			//hashSet.add(value1);
			hashSet.add(value2);
			vs.put(i, hashSet);
			if (value2.equals(false)&&value.equals(true)) {
				c=c+1;
			}
		}
		vs.keySet().forEach(k->{
			if (vs.get(k).size()>1) {
				//count=count+1;
				Object value = t3.get(k).value("positive");
				if (value.equals(true)) {
					count=count+1;
				}
				System.out.println(k+":"+vs.get(k)+":"+t3.get(k).value("positive")+":"+t2.get(k).value("positive"));
			}
		});
		//System.out.println(c);
		//System.out.println(count);		
	}
	
	@Test
	public void test2() {
		BasicDataSetImpl t1=DataSetIO.load("file://D:/sw2/data/stage_2_train.csv").as(BasicDataSetImpl.class);
		t1=(BasicDataSetImpl) t1.mergeBy("ImageId");
		t1=(BasicDataSetImpl) t1.filter("EncodedPixels", x->{
			Object[] vl=(Object[]) x;
			return vl.length>1;
		});
		t1.setIdColumn("ImageId");
		IColumn column = t1.getColumn("ImageId");
		
		Collection<Object> values = column.values();
		System.out.println(values.size());
		System.out.println(new HashSet<>(values).size());
		
		BasicDataSetImpl t2=DataSetIO.load("file://D:/sw2/data/train-rle.csv").as(BasicDataSetImpl.class);
		t2.setIdColumn("ImageId");
		
		HashSet hashSet = new HashSet(t1.getColumn("ImageId").values());
		HashSet hashSet2 = new HashSet(t2.getColumn("ImageId").values());
		
		hashSet.removeAll(hashSet2);
		
		BasicDataSetImpl t3=DataSetIO.load("file://D:/s/subm_best.csv").as(BasicDataSetImpl.class);
		HashSet hashSet3 = new HashSet(t3.getColumn("ImageId").values());
		
		
		t3.setIdColumn("ImageId");
		IDataSet additions = t2.compare(t1).additions();
		int size = additions.items().size();
		
		
		IDataSetDelta compare = additions.compare(t3);
		assertTrue(compare.isEmpty());
	}
}