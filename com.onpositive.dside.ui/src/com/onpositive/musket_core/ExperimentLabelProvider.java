package com.onpositive.musket_core;

import java.text.NumberFormat;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.ui.richtext.IRichLabelProvider;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.model.ui.richtext.StyledString.Style;

public class ExperimentLabelProvider implements IRichLabelProvider{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getDescription(Object arg0) {
		return null;
	}

	@Override
	public String getText(IHasMeta arg0, Object arg1, Object arg2) {
		return arg2.toString();
	}

	@Override
	public StyledString getRichTextLabel(Object arg0) {
		StyledString styledString = new StyledString(arg0.toString());
		Experiment ex=(Experiment) arg0;
		if (ex.getConfig().containsKey("testSplit")) {
			Object object = ex.getConfig().get("testSplit");			
			styledString.append(" holdout:"+NumberFormat.getInstance().format(object), new Style("dark gray", null));
		}
		if (ex.getConfig().containsKey("num_seeds")) {
			Object object = ex.getConfig().get("num_seeds");			
			styledString.append(" seeds:"+NumberFormat.getInstance().format(object), new Style("dark gray", null));
		}
		return styledString;
	}

}
