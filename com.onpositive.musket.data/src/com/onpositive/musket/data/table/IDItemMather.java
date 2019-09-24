package com.onpositive.musket.data.table;

import java.util.HashMap;

import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IItemMatcher;

public class IDItemMather implements IItemMatcher{

	protected HashMap<Object, IItem>items=new HashMap<Object, IItem>();

	@Override
	public IItem map(IItem item) {
		return items.get(item.id());
	}
}
