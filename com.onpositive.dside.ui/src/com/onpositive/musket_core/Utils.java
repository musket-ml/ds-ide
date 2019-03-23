package com.onpositive.musket_core;

import java.util.ArrayList;
import java.util.function.Function;

public class Utils {

	public static<T,A> ArrayList<A> asList(IList<T>t,Function<T,A>c){
		ArrayList<A>r=new ArrayList<>();
		int size = t.size();
		for (int i=0;i<size;i++) {
			r.add(c.apply(t.get(i)));
		}
		return r;
	}
}
