package com.onpositive.musket.data.table;

import com.onpositive.semantic.model.ui.generic.IKnowsImageObject;

public abstract class AbstractColumnType implements IColumnType,IKnowsImageObject{

	protected String image;
	
	
	public AbstractColumnType(String image, String id, String caption) {
		super();
		this.image = image;
		this.id = id;
		this.caption = caption;
	}

	protected String id;
	protected String caption;
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public String caption() {
		return caption;
	}
	
	@Override
	public String getImageID() {
		return image;
	}

	@Override
	public String toString() {
		return caption;
	}
}
