package com.onpositive.musket.data.generic;

import java.util.Map;

import com.onpositive.musket.data.columntypes.ColumnLayout;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDataSetFactory;
import com.onpositive.musket.data.core.IDataSet;

public class GenericDataSetFactory implements IDataSetFactory{

	@Override
	public String caption() {
		return "Generic dataset factory";
	}

	@Override
	public double estimate(DataSetSpec parameterObject) {
		return 1;
	}

	@Override
	public IDataSet create(DataSetSpec spec, Map<String, Object> options) {
		
		if (options!=null) {
			Object object = options.get("layout");
			ColumnLayout l1=new ColumnLayout(spec, object);
			spec=new DataSetSpec(l1, l1.getNewDataSet(),spec.prj,spec.answerer);
			GenericDataSet genericDataSet = new GenericDataSet(spec, spec.tb);
			genericDataSet.setSettings(null, options);
			return genericDataSet;
		}
		GenericDataSet genericDataSet = new GenericDataSet(spec, spec.tb);
		return genericDataSet;
	}

}
