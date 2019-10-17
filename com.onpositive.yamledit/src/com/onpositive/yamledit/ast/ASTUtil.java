package com.onpositive.yamledit.ast;

import org.apache.commons.lang3.Range;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public class ASTUtil {
	public static Range<Integer> getRange(String key, IHasLocation element, boolean onKey) {
		NodeTuple tuple = element.findInKey(key);
		if (tuple!=null) {
			Node node = onKey?tuple.getKeyNode():tuple.getValueNode();
			return Range.between(node.getStartMark().getIndex(), node.getEndMark().getIndex());
		}
		return null;

	}
}
