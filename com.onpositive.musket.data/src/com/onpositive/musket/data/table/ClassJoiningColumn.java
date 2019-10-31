package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ClassJoiningColumn implements IColumn{

	private ArrayList<IColumn> columns;
	private String id;
	private ITabularDataSet owner;
	private Collection<Object> values;

	public ClassJoiningColumn(ArrayList<IColumn>cs,String id,ITabularDataSet owner) {
		this.columns=cs;
		this.id=id;
		this.owner=owner;
		this.values=new ArrayList<>();
		for (IColumn c:cs) {
			this.values.add(c.id());
		}
	}
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public String caption() {
		return id;
	}

	@Override
	public Class<?> clazz() {
		return String.class;
	}

	@Override
	public Object getValue(ITabularItem item) {
		return getValueAsString(item);
	}

	@Override
	public void setValue(ITabularItem item, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getValueAsString(ITabularItem i) {
		ArrayList<String>sm=new ArrayList<>();
		for (IColumn c:this.columns) {
			String valueAsString = c.getValueAsString(i).toLowerCase();
			if (valueAsString.equals("1")||valueAsString.equals("true")||valueAsString.equals("y")||valueAsString.equals("yes")) {
				sm.add(c.id());
			}
		}
		return sm.stream().collect(Collectors.joining(" "));
	}

	@Override
	public ITabularDataSet owner() {
		return owner;
	}
	

	@Override
	public IColumn clone() {
		return new ClassJoiningColumn(columns, id, owner);
	}

	@Override
	public Collection<Object> values() {
		ArrayList<Object>object=new ArrayList<>();
		this.owner().items().forEach(v->{
			object.add(getValue(v));
		});
		return object;
	}

}
