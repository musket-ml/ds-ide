package com.onpositive.musket.data.table;

public class SubColumn extends Column{

	private int subnum;
	public SubColumn(String id, String caption, int num, Class<?> clazz,int subNum) {
		super(id, caption, num, clazz);
		this.subnum=subNum;
	}
	@Override
	public Object getValue(ITabularItem item) {
		BasicItem bi=(BasicItem) item;
		String object = bi.values[num].toString();
		String[] vals=object.split("_");
		if (vals.length<=subnum) {
			return null;
		}
		return vals[subnum];
	}
	@Override
	public void setValue(ITabularItem item, Object value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String id() {
		return super.id()+":"+subnum;
	}
}
