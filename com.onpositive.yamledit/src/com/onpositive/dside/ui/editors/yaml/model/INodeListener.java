package com.onpositive.dside.ui.editors.yaml.model;

public interface INodeListener {

	public void updated(ModelNode node,Object newValue,String property);
}
