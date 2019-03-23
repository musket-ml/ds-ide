package com.onpositive.musket_core;

import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.ui.richtext.IRichLabelProvider;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.model.ui.richtext.StyledString.Style;

public class ExperimentDescriptionLabelProvider implements IRichLabelProvider{

	@Override
	public String getDescription(Object arg0) {
		return null;
	}

	@Override
	public String getText(IHasMeta arg0, Object arg1, Object arg2) {
		return getRichTextLabel(arg2).toString();
	}

	@Override
	public StyledString getRichTextLabel(Object arg0) {
		ExperimentDescription d=(ExperimentDescription)arg0;
		StyledString styledString = new StyledString(d.description);
		
		Experiment exp=((ExperimentDescription) arg0).experiment;
		styledString.append("(primary metric: ",new Style("dark green",null));
		styledString.append(exp.getPrimaryMetric(),new Style("dark blue",null));
		if (exp.getConfig().containsKey("architecture")) {
			styledString.append(" architecture: ",new Style("dark green",null));
			styledString.append(exp.getConfig().get("architecture").toString(),new Style("dark blue",null));
		}
		styledString.append(" dataset: ",new Style("dark green",null));
		String dataSet = exp.getDataSet();
		styledString.append(dataSet,new Style("dark blue",null));
		styledString.append(")",new Style("dark green",null));
		
		return styledString;
	}

}
