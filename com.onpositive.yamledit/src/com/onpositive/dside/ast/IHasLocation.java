package com.onpositive.dside.ast;

import org.yaml.snakeyaml.nodes.NodeTuple;

public interface IHasLocation {

	public int getStartOffset() ;

	public int getEndOffset();
	
	public NodeTuple findInKey(String key);

	public IHasLocation getParent();
}
