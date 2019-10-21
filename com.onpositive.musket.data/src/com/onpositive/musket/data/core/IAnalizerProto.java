package com.onpositive.musket.data.core;

import java.util.HashMap;

public interface IAnalizerProto extends IProto,Comparable<IAnalizerProto>{

	IAnalizeResults perform(HashMap<String, Object> analzierArgs, IDataSet dataset);

	int score();
	
	
	default int compareTo(IAnalizerProto o) {
		return o.score()-this.score();
	}
}
