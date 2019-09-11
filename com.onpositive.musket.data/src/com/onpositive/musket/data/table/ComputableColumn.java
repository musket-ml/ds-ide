package com.onpositive.musket.data.table;

import java.util.function.Function;

public class ComputableColumn extends Column implements IColumn{

	private Function<ITabularItem, ?> f;

	@SuppressWarnings("unchecked")
	public ComputableColumn(String id, String caption, int num, Class<?> clazz,Function<? extends ITabularItem, Object>f) {
		super(id, caption, num, clazz);
		this.f=(Function<ITabularItem, ?>) f;
	}
	
	@Override
	public Object getValue(ITabularItem item) {
		return (Object)f.apply(item);
	}

}