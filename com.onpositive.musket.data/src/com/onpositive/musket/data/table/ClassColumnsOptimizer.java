package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.musket.data.images.MultiClassClassificationItem;

public class ClassColumnsOptimizer {

	public static List<IColumn>optimize(Collection<IColumn>clazzColumn,IQuestionAnswerer anwerer) {
		ArrayList<IColumn>binaryColumns=new ArrayList<>();
		ArrayList<IColumn>multiClassColumns=new ArrayList<>();
		ArrayList<IColumn>categoricalColumns=new ArrayList<>();
		
		ArrayList<IColumn>result=new ArrayList<>();
		for (IColumn c:clazzColumn) {
			if (c.isBinaryColumn()) {
				binaryColumns.add(c);
			}
			else if (MultiClassClassificationItem.isMultiClass(c)) {
				multiClassColumns.add(c);
			}
			else {
				categoricalColumns.add(c);
			}
		}
		boolean binaryConsumed=false;
		if (!binaryColumns.isEmpty()&&binaryColumns.size()>1) {
			boolean askQuestion = anwerer.askQuestion("Do you want to join binary columns: "+name(binaryColumns,", "), true);
			if (askQuestion) {
				result.add(new ClassJoiningColumn(binaryColumns, name(binaryColumns,"|"), clazzColumn.iterator().next().owner()));
				binaryConsumed=true;
			}
		}
		if (!binaryConsumed) {
			result.addAll(binaryColumns);
		}
		
		return result;
	}

	protected static String name(ArrayList<IColumn> binaryColumns,String sep) {
		return binaryColumns.stream().map(x->x.id()).collect(Collectors.joining(sep));
	}
	
	public static IColumn createCompositeColumn(ArrayList<IColumn> clazzColumns) {
		LinkedHashSet<Object> allValues = new LinkedHashSet<>();
		int tS = 0;
		ArrayList<IColumn>nc=new ArrayList<>();
		for (IColumn m : clazzColumns) {
			ArrayList<Object> uniqueValues = m.uniqueValues();
			if (uniqueValues.size()>1) {
				nc.add(m);
			}
			tS = tS + uniqueValues.size();
			allValues.addAll(uniqueValues);
		}
		boolean uniqueValues = tS == allValues.size();
		IColumn clazzColumn=null;
		if (uniqueValues) {
			clazzColumn = new ComputableColumn("clazz", "clazz", -1, String.class, v -> {
				return nc.stream().map(x -> x.getValueAsString(v).trim()).collect(Collectors.joining(" "));
			});
		} else {
			clazzColumn = new ComputableColumn("clazz", "clazz", -1, String.class, v -> {
				return nc.stream().map(x -> doMap(v, x)).collect(Collectors.joining(" "));
			});
		}
		return clazzColumn;
	}
	

	protected static String doMap(ITabularItem v, IColumn x) {
		return x.id() + "_" + x.getValueAsString(v).trim();
	}
	
	
}
