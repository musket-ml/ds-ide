package com.onpositive.yamledit.ast;

import java.util.HashSet;
import java.util.List;

import org.aml.typesystem.Status;
import org.aml.typesystem.values.IArray;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import com.onpositive.yamledit.model.IValidator;

public class LayerValidator implements IValidator {

	private final class LocationInfo implements IHasLocation {
		private final NodeTuple t;

		private LocationInfo(NodeTuple t) {
			this.t = t;
		}

		@Override
		public int getStartOffset() {
			return t.getKeyNode().getStartMark().getIndex();
		}

		@Override
		public IHasLocation getParent() {
			return null;
		}

		@Override
		public int getEndOffset() {
			return t.getValueNode().getEndMark().getIndex()+1;
		}

		@Override
		public NodeTuple findInKey(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LocationInfo other = (LocationInfo) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		private LayerValidator getOuterType() {
			return LayerValidator.this;
		}
	}

	@Override
	public Status validate(ASTElement element) {
		Object property = element.getProperty("inputs");
		HashSet<String> names = new HashSet<>();
		Status res = new Status(0, 0, "");
		if (property != null) {
			if (property instanceof IArray) {
				IArray a = (IArray) property;
				for (int i = 0; i < a.length(); i++) {
					Object item = a.item(i);
					if (item != null) {
						names.add(item.toString());
					}
				}
			}
		}
		gatherNames(element.node, names, res);
		res.setSeverity(0);
		return res;
	}

	public void gatherNames(Node el, HashSet<String> names, Status status) {
		if (el instanceof SequenceNode) {
			SequenceNode s = (SequenceNode) el;
			s.getValue().forEach(v -> gatherNames(v, names, status));
		}
		if (el instanceof MappingNode) {
			MappingNode ms = (MappingNode) el;
			List<NodeTuple> value = ms.getValue();
			for (NodeTuple t : value) {
				Node keyNode = t.getKeyNode();
				if (keyNode instanceof ScalarNode) {
					ScalarNode sn = (ScalarNode) keyNode;

					String name = sn.getValue();
					if (name.equals("name")) {
						Node vn = t.getValueNode();
						if (vn instanceof ScalarNode) {
							if (!names.add(((ScalarNode) vn).getValue())) {
								String message = "layer names should be unique";
								reportError(status, t, message);
							}
						}
					} else if (name.equals("inputs")) {
						Node valueNode = t.getValueNode();
						if (valueNode instanceof ScalarNode) {
							ScalarNode sc = (ScalarNode) valueNode;
							if (!names.contains(sc.getValue())) {
								reportError(status, t, "unknown input:" + sc.getValue());
							}
						} else if (valueNode instanceof SequenceNode) {
							SequenceNode sa = (SequenceNode) valueNode;
							for (int i = 0; i < sa.getValue().size(); i++) {
								Node node = sa.getValue().get(i);
								if (node instanceof ScalarNode) {
									ScalarNode sc = (ScalarNode) node;
									if (!names.contains(sc.getValue())) {
										reportError(status, t, "unknown input:" + sc.getValue());
									}
								}
							}
						}
					} else {
						gatherNames(t.getValueNode(), names, status);
					}
				}
			}
		}
	}

	private void reportError(Status status, NodeTuple t, String message) {
		status.addSubStatus(new Status(4, 4, message, new LocationInfo(t)));
	}
}
