package com.onpositive.musket.data.generic;

import java.awt.Image;
import java.text.NumberFormat;
import java.util.List;

import com.onpositive.musket.data.columntypes.NumberColumn;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.ITabularItem;

public class GenericItemWithPrediction extends GenericItem{

	protected GenericItem prediction;
	
	public GenericItemWithPrediction(GenericDataSet ds, ITabularItem base,GenericItem prediction) {
		super(ds, base);
		this.prediction=prediction;
	}

	public GenericItem getPrediction() {
		return prediction;
	}

	@Override
	public Image getImage() {
		if (this.prediction!=null) {
			System.out.println("A");
		}
		return super.getImage();
	}
	
	protected String getTextValue(int mxch, IColumn column) {
		String value = column.getValueAsString(base);
		if (value.length() > mxch) {
			value = value.substring(0, mxch) + "...";
		}
		String valueAsString = column.getValueAsString(prediction.base);
		if (!value.equals(valueAsString)) {
			 value=value+"<>"+valueAsString;
		}				
		return value;
	}

	protected void appendSimpleValue(StringBuilder bld, IColumn column, Class<? extends IColumnType> preferredType) {
		String value = column.getValueAsString(base);
		String value0 = column.getValueAsString(base);
		if (preferredType == NumberColumn.class) {
			try {
				value = NumberFormat.getInstance().format(Double.parseDouble(value));
			} catch (NumberFormatException e) {
			}
		}
		if (value.length() > 100) {
			value = value.substring(0, 100);
		}
		bld.append("<FONT COLOR=BLUE>" + StringUtils.encodeHtml(value) + "</FONT>");
		String valueAsString = column.getValueAsString(prediction.base);
		if (!value0.equals(valueAsString)) {
			if (preferredType == NumberColumn.class) {
				try {
					valueAsString = NumberFormat.getInstance().format(Double.parseDouble(valueAsString));
				} catch (NumberFormatException e) {
				}
			}
			bld.append(" &lt;&gt; <FONT COLOR=RED>" + StringUtils.encodeHtml(valueAsString) + "</FONT>");	
		}
		bld.append(" ");
	}

	public boolean allMatch() {
		List<? extends IColumn> columns = this.ds.getSpec().columns();
		for (IColumn c:columns) {
			String valueAsString = c.getValueAsString(this.base);
			if (this.prediction==null) {
				return false;
			}
			String valueAsString2 = c.getValueAsString(this.prediction.base);
			if (!valueAsString.equals(valueAsString2)) {
				return false;
			}
		}
		return true;
	}
}
