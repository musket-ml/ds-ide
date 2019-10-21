package com.onpositive.mmdetection.wrappers;

import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.IHasMeta;

public class PathLabelProvider implements ITextLabelProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3419755403469303451L;

	@Override
	public String getDescription(Object arg0) {
		return null;
	}

	@Override
	public String getText(IHasMeta arg0, Object arg1, Object arg2) {
		if (!(arg2 instanceof MMDetCfgData)) {
			return "not a config";
		}
		return ((MMDetCfgData)arg2).getPath();
	}
	
}
