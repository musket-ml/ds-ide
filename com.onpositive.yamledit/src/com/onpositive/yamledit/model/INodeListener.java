package com.onpositive.yamledit.model;

public interface INodeListener {

	public void updated(ModelNode node,Object newValue,String property);
}
