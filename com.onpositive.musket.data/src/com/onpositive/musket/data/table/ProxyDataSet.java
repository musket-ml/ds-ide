package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.List;

public class ProxyDataSet extends BasicDataSetImpl{

	public ProxyDataSet(ArrayList<? extends ITabularItem> items, List<? extends IColumn> cs) {
		super(items, cs);
	}

	@Override
	public List<? extends ITabularItem> items() {
		return super.items();
	}
}
