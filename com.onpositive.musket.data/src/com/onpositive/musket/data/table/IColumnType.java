package com.onpositive.musket.data.table;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.semantic.model.ui.generic.IKnowsImageObject;

public interface IColumnType extends IKnowsImageObject{

    public enum ColumnPreference{
    	STRICT,MAYBE,NEVER
    }
    
    public ColumnPreference is(IColumn c,DataProject prj,IQuestionAnswerer answerer);

    public String id();
    
    public String caption();

	public String typeId(IColumn column);

}
