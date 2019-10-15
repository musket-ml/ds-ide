package com.onpositive.musket.data.table;

import java.util.function.Function;

public class ComputableColumn extends Column implements IColumn{

	private Function<ITabularItem, ?> f;
	
	protected Function<Object,Object> value=null;

	@SuppressWarnings("unchecked")
	public ComputableColumn(String id, String caption, int num, Class<?> clazz,Function<? extends ITabularItem, Object>f) {
		super(id, caption, num, clazz);
		this.f=(Function<ITabularItem, ?>) f;
	}
	
	@Override
	public Object getValue(ITabularItem item) {
		Object apply = (Object)f.apply(item);
		if (value!=null) {
			return value.apply(apply);
		}
		return apply;
	}

	public ComputableColumn map(Function<Object,Object>vm) {
		ComputableColumn computableColumn = new ComputableColumn(this.id, this.caption, this.getNum(), this.getClazz(), (Function<? extends ITabularItem, Object>) f);
		if (this.value!=null) {
			computableColumn.value=this.value.andThen(vm);
		}
		else {
			computableColumn.value=vm;
		}
		return computableColumn;
	}
}