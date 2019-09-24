package com.onpositive.musket.data.table;

import java.util.Collection;

public class RLERepresenter implements IColumnRepresenter {


	@Override
	public boolean like(IColumn c) {
		boolean isOk=true;
		Collection<Object> values = c.values();
		for (Object o:values) {
			if (o==null) {
				continue;
			}
			if (!o.toString().trim().equals("-1")&&!o.toString().trim().isEmpty()) {
				String[] split = o.toString().trim().split(" ");
				if (split.length%2!=0) {
					return false;
				}
				for (String s:split) {
					try {
					Integer.parseInt(s);
					}catch (Exception e) {
						return false;
					}
				}
			}
		}
		return isOk;
	}

	@Override
	public Object adaptValue(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

}
