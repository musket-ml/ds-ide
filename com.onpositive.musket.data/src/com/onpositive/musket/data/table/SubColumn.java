package com.onpositive.musket.data.table;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SubColumn extends Column{

	private int subnum;
	private int endIndex=Integer.MIN_VALUE;
	public SubColumn(String id, String caption, int num, Class<?> clazz,int subNum) {
		super(id, caption, num, clazz);
		this.subnum=subNum;
	}
	public SubColumn(String id, String s, Column c, int sn) {
		this(id,s,c.getNum(),c.getClazz(),sn);
		this.owner=c.owner;
	}
	public SubColumn(String id, String s, Column c, int sn,int endIndex) {
		this(id,s,c.getNum(),c.getClazz(),sn);
		this.owner=c.owner;
		this.endIndex=endIndex;
	}
	@Override
	public Object getValue(ITabularItem item) {
		BasicItem bi=(BasicItem) item;
		String object = bi.values[getNum()].toString();
		String[] vals=object.split("_");
		if (vals.length<=subnum) {
			return null;
		}
		if (this.endIndex!=Integer.MIN_VALUE) {
			int e=this.endIndex;
			if (e==-1) {
				e=vals.length;
			}
			ArrayList<String>vls=new ArrayList<>();
			for (int i=subnum;i<e;i++) {
				vls.add(vals[i]);
			}
			return vls.stream().collect(Collectors.joining("_"));
		}
		return vals[subnum];
	}
	@Override
	public void setValue(ITabularItem item, Object value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String id() {
		if (endIndex!=Integer.MIN_VALUE) {
			return super.id()+":"+subnum+"-"+endIndex;
		}
		return super.id()+":"+subnum;
	}
}
