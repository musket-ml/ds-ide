package com.onpositive.musket.data.columntypes;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.IQuestionAnswerer;

public interface ISmartColumnType extends IColumnType{

    public SmartColumnPref is2(IColumn c,DataProject prj,IQuestionAnswerer answerer);
}
