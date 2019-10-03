package com.onpositive.yamledit.model;

import java.util.List;
import java.util.Map;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.ui.richtext.IRichLabelProvider;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.model.ui.richtext.StyledString.Style;

public class ModelNodeLabelProvider implements IRichLabelProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getText(IHasMeta meta, Object parent, Object object) {
		return getRichTextLabel(object).toString();
	}

	@Override
	public String getDescription(Object object) {
		return getRichTextLabel(object).toString();
	}

	@Override
	public StyledString getRichTextLabel(Object arg0) {
		ModelNode node = (ModelNode) arg0;
		StyledString res = new StyledString();

		if (node.key != null) {
			res.append(node.key);
		} else {
			if (node.object instanceof String) {
				return new StyledString(node.object.toString());
			}
		}
		if (node.object instanceof Map) {
			Map<Object, Object> m = (Map) node.object;

			if (!m.isEmpty()) {
				// res.append("(", new Style("dark green", null));
				m.entrySet().forEach(f -> {
					Object value = f.getValue();
					if (value instanceof Boolean || value instanceof String || value instanceof Number) {
						res.append(" " + f.getKey().toString() + ":", new Style("dark green", null));
						res.append(value.toString(), new Style("dark blue", null));
					}
				});
				// res.append(")", new Style("dark green", null));
			}
		}
		if (node.object != null) {
			if (node.object.equals(node.key)) {
				return res;
			}
		}
		if (node.object instanceof Number || node.object instanceof String || node.object instanceof Boolean) {
			res.append(":", new Style("dark green", null));

			res.append(node.object.toString(), new Style("dark blue", null));
		}
		if (node.object instanceof List) {
			if (!node.hasChildren()) {
				List l = (List) node.object;
				if (!l.isEmpty()) {
					// res.append("(", new Style("dark green", null));
					for (Object f : l) {
						if (f instanceof Boolean || f instanceof String || f instanceof Number) {
							res.append(" ");
							res.append(f.toString(), new Style("dark blue", null));
						}
					}
					// res.append(")", new Style("dark green", null));
				}
			}
		}
		return res;
	}

}
