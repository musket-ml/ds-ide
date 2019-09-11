package com.onpositive.musket.data.core;

import java.util.HashMap;

public interface IAnalizerProto extends IProto{

	IAnalizeResults perform(HashMap<String, Object> analzierArgs, IDataSet dataset);

}
