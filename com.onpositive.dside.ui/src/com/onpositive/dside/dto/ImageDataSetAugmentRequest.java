package com.onpositive.dside.dto;

import com.onpositive.dside.ui.ModelEvaluationSpec;

public class ImageDataSetAugmentRequest extends AbstractDataSetRequest {

	/**
	 * Yaml string with augmentation configuration, e.g.
	 * <pre>
	 * <code>
	 * augmentation: 
  	 *	 Fliplr: 0.5 
  	 *	 Flipud: 0.5 
	 * </code>
	 * </pre>
	 */
	protected String augmentationConfig;

	public ImageDataSetAugmentRequest(ModelEvaluationSpec model, String dataset, String experimentPath, String augmentationConfig) {
		super(model, dataset, experimentPath);
		this.augmentationConfig = augmentationConfig;
	}

	public String getAugmentationConfig() {
		return augmentationConfig;
	}

	public void setAugmentationConfig(String augmentationConfig) {
		this.augmentationConfig = augmentationConfig;
	}
	
}
