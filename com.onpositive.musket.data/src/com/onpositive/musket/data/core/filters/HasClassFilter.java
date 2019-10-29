package com.onpositive.musket.data.core.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.onpositive.musket.data.core.ICompletableFilter;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetFilter;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.images.IMulticlassClassificationDataSet;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.table.IHasLabels;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;


@Caption("Has Class")
public class HasClassFilter implements IDataSetFilter<IMulticlassClassificationDataSet>,ICompletableFilter{

	protected String id;
	
	
	public HasClassFilter(String id) {
		super();
		this.id = id;
		if (id==null) {
			this.id="";
		}
	}

	@Override
	public boolean test(IItem arg0) {
		IMulticlassClassificationItem it=(IMulticlassClassificationItem) arg0;
		ArrayList<String> classes = it.classes();
		
		return classes.contains(id);
	}

	@Override
	public Collection<String> options(IDataSet ds) {
		IMulticlassClassificationDataSet vv=(IMulticlassClassificationDataSet) ds;
		List<String> classNames = vv.classNames();
		if (ds instanceof IHasLabels) {
			IHasLabels lm=(IHasLabels) ds;
			LabelsSet ss=lm.labels();
			ArrayList<String>rs=new ArrayList<>();
			for (String s:classNames) {
				rs.add(ss.map(s));
			}
			return rs;
		}
		return classNames;
	}
}
